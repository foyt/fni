package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.junit.Test;
import com.icegreen.greenmail.util.GreenMail;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet(id = "event", before = { "illusion-event-open-setup.sql" }, after = { "illusion-event-open-teardown.sql"}),
  @DefineSqlSet(id = "event-unpublished", before = { "illusion-event-open-unpublished-setup.sql" }, after = { "illusion-event-open-unpublished-teardown.sql"}),
  @DefineSqlSet(id = "event-participant", before = {"illusion-event-open-participant-setup.sql" }, after = {"illusion-event-open-participant-teardown.sql"}),
  @DefineSqlSet(id = "event-organizer", before = {"illusion-event-open-organizer-setup.sql" }, after = {"illusion-event-open-organizer-teardown.sql"}),
  @DefineSqlSet(id = "event-invited", before = {"illusion-event-open-invited-setup.sql" }, after = {"illusion-event-open-invited-teardown.sql"}),
  @DefineSqlSet(id = "event-forum", before = { "illusion-event-open-forum-setup.sql" }, after = {"illusion-event-open-forum-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-participants", before = { "illusion-event-open-forum-participants-setup.sql" }, after = {"illusion-event-open-forum-participants-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-visible", before = { "illusion-event-open-forum-visible-setup.sql" }, after = {"illusion-event-open-forum-visible-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-posts", before = { "illusion-event-open-forum-posts-setup.sql" }, after = {"illusion-event-open-forum-posts-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-organizer-posts", before = { "illusion-event-open-forum-organizer-posts-setup.sql" }, after = {"illusion-event-open-forum-organizer-posts-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-watchers", before = { "illusion-event-open-forum-watchers-setup.sql" }, after = {"illusion-event-open-forum-watchers-teardown.sql"}),
  @DefineSqlSet(id = "event-custom", before = { "illusion-event-open-custom-setup.sql" }, after = {"illusion-event-open-custom-teardown.sql"}),
})
public class IllusionEventForumTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-participants" })
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/event-forum");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible"})
  public void testLoginNotRequired() throws Exception {
    testTitle("/illusion/event/openevent/event-forum", "Illusion - Open Event");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-forum" })
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/event-forum/");
    testNotFound("/illusion/event/noevent/event-forum");
    testNotFound("/illusion/event/noevent//event-forum");
    testNotFound("/illusion/event/noevent/*/event-forum");
    testNotFound("/illusion/event/1/event-forum");
    testNotFound("/illusion/event///event-forum");
    testNotFound("/illusion/event//*/event-forum");
    testNotFound("/illusion/event/~/event-forum");  
  }
  
  @Test
  @SqlSets({ "basic-users", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-participants" })
  public void testLoggedInParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/event-forum", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "forum");
  }
  
  @Test
  @SqlSets({ "basic-users", "illusion-basic", "event", "event-organizer", "event-forum", "event-forum-participants" })
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/event-forum", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "forum");
  }
  
  @Test
  @SqlSets({ "basic-users", "illusion-basic", "event", "event-invited", "event-forum", "event-forum-participants" })
  public void testLoggedInInvited() throws Exception {
    loginInternal("librarian@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/event-forum", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "forum");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-forum" })
  public void testHiddenLoggedIn() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/event-forum");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-custom", "event-participant", "event-forum", "event-forum-visible"})
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/event-forum");
    testTitle("Illusion - Open Event");
  }

  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-custom", "event-participant", "event-forum", "event-forum-participants"})
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/event-forum");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("user@foyt.fi", "pass");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-custom", "event-participant", "event-forum", "event-forum-visible"})
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("user@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/event-forum");
    testTitle("Illusion - Open Event");
    assertMenuItems();
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-custom", "event-organizer", "event-forum", "event-forum-visible"})
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/event-forum");
    testTitle("Illusion - Open Event");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1)").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/event-forum", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(7)").getAttribute("href"));
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-unpublished", "event-organizer", "event-forum", "event-forum-visible"})
  public void testUnpublishedAccessDenied() throws UnsupportedEncodingException {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/event-forum");
    logout();
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/event-forum");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-posts", "event-forum-organizer-posts"})
  public void testMayModifyPost() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion/event/openevent/event-forum");
    testTitle("Illusion - Open Event");
    assertSelectorCount(".illusion-forum-post", 4);
    assertSelectorPresent(".illusion-forum-post[data-post-id=\"20100\"] .illusion-forum-post-edit");
    assertSelectorPresent(".illusion-forum-post[data-post-id=\"20101\"] .illusion-forum-post-edit");
    assertSelectorNotPresent(".illusion-forum-post[data-post-id=\"20102\"] .illusion-forum-post-edit");
    assertSelectorNotPresent(".illusion-forum-post[data-post-id=\"20103\"] .illusion-forum-post-edit");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-posts", "event-forum-organizer-posts"})
  public void testPost() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion/event/openevent/event-forum");
    testTitle("Illusion - Open Event");
    assertSelectorCount(".illusion-forum-post", 4);
    waitForSelectorVisible(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
    switchFrame(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
    typeSelectorInputValue(".cke_editable", "post content");    
    switchDefault();
    clickSelector(".illusion-forum-post-reply");
    waitForSelectorVisible(".illusion-forum-posts .illusion-forum-post:nth-child(5)");
    assertSelectorTextIgnoreCase(".illusion-forum-posts .illusion-forum-post:nth-child(5) .illusion-forum-post-content p", "post content");
    clickSelector(".illusion-forum-stop-watching-link");
    waitForSelectorVisible(".illusion-forum-watch-link");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible" })
  public void testWatchNotLogged() throws MessagingException, IOException {
    navigate("/illusion/event/openevent/event-forum");
    assertSelectorNotVisible(".illusion-forum-stop-watching-link");
    assertSelectorNotVisible(".illusion-forum-watch-link");
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-watchers"})
  public void testNotification() throws MessagingException, IOException {
    acceptCookieDirective();
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      
      navigate("/illusion/event/openevent/event-forum");
      
      waitForSelectorVisible(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
      switchFrame(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
      waitForSelectorVisible(".cke_editable");
      typeSelectorInputValue(".cke_editable", "post content");    
      switchDefault();
      clickSelector(".illusion-forum-post-reply");
      waitForSelectorVisible(".illusion-forum-posts .illusion-forum-post");
      
      assertEquals(1, greenMail.getReceivedMessages().length);
      assertEquals("Notification about forum post", greenMail.getReceivedMessages()[0].getSubject());

      clickSelector(".illusion-forum-stop-watching-link");
      waitForSelectorVisible(".illusion-forum-watch-link");
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible"})
  public void testStartWatch() throws MessagingException, IOException {
    GreenMail greenMail = startSmtpServer();
    try {
      acceptCookieDirective();
      
      loginInternal("user@foyt.fi", "pass");
      navigate("/illusion/event/openevent/event-forum");
      waitForSelectorVisible(".illusion-forum-watch-link");
      
      assertSelectorNotVisible(".illusion-forum-stop-watching-link");
      assertSelectorVisible(".illusion-forum-watch-link");

      clickSelector(".illusion-forum-watch-link");
      waitForSelectorVisible(".illusion-forum-stop-watching-link");
      
      assertSelectorVisible(".illusion-forum-stop-watching-link");
      assertSelectorNotVisible(".illusion-forum-watch-link");
      
      logout();
      loginInternal("admin@foyt.fi", "pass");
      navigate("/illusion/event/openevent/event-forum");
      
      waitForSelectorVisible(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
      switchFrame(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
      waitForSelectorVisible(".cke_editable");
      typeSelectorInputValue(".cke_editable", "post content");    
      switchDefault();
      clickSelector(".illusion-forum-post-reply");
      waitForSelectorVisible(".illusion-forum-posts .illusion-forum-post");
      
      assertEquals(1, greenMail.getReceivedMessages().length);
      assertEquals("Notification about forum post", greenMail.getReceivedMessages()[0].getSubject());
      clickSelector(".illusion-forum-stop-watching-link");
      waitForSelectorVisible(".illusion-forum-watch-link");

      logout();
      loginInternal("user@foyt.fi", "pass");
      navigate("/illusion/event/openevent/event-forum");

      clickSelector(".illusion-forum-stop-watching-link");
      waitForSelectorVisible(".illusion-forum-watch-link");
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible"})
  public void testStopWatch() throws MessagingException, IOException {
    GreenMail greenMail = startSmtpServer();
    try {
      acceptCookieDirective();
      
      loginInternal("user@foyt.fi", "pass");
      navigate("/illusion/event/openevent/event-forum");
      waitForSelectorVisible(".illusion-forum-watch-link");
      
      assertSelectorNotVisible(".illusion-forum-stop-watching-link");
      assertSelectorVisible(".illusion-forum-watch-link");

      clickSelector(".illusion-forum-watch-link");
      waitForSelectorVisible(".illusion-forum-stop-watching-link");
      
      assertSelectorVisible(".illusion-forum-stop-watching-link");
      assertSelectorNotVisible(".illusion-forum-watch-link");

      clickSelector(".illusion-forum-stop-watching-link");
      waitForSelectorVisible(".illusion-forum-watch-link");
      
      assertSelectorNotVisible(".illusion-forum-stop-watching-link");
      assertSelectorVisible(".illusion-forum-watch-link");
      
      logout();
      loginInternal("admin@foyt.fi", "pass");
      navigate("/illusion/event/openevent/event-forum");
      
      waitForSelectorVisible(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
      switchFrame(".illusion-forum-post-editor-container .cke_wysiwyg_frame");
      waitForSelectorVisible(".cke_editable");
      typeSelectorInputValue(".cke_editable", "post content");    
      switchDefault();
      clickSelector(".illusion-forum-post-reply");
      waitForSelectorVisible(".illusion-forum-posts .illusion-forum-post");
      
      assertEquals(0, greenMail.getReceivedMessages().length);
      
      clickSelector(".illusion-forum-stop-watching-link");
      waitForSelectorVisible(".illusion-forum-watch-link");
    } finally {
      greenMail.stop();
    } 
  }
}
