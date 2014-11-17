package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlAfter;
import fi.foyt.fni.test.SqlBefore;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "illusion-event-oai", 
    before = {"illusion-event-oai-setup.sql"}, 
    after = {"illusion-event-oai-teardown.sql"}
  )
})
public class IllusionEventIndexTestsBase extends AbstractUITest {
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testNotLoggedIn() throws Exception {
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorPresent(".illusion-event-join-button");
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testNotFound() throws Exception {
    testNotFound("/illusion/event/openevent/");
    testNotFound("/illusion/event/noevent");
    testNotFound("/illusion/event/noevent/");
    testNotFound("/illusion/event/noevent/*");
    testNotFound("/illusion/event/1");
    testNotFound("/illusion/event//");
    testNotFound("/illusion/event//*");
    testNotFound("/illusion/event/~");
  }

  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedIn() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorPresent(".illusion-event-join-button");
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql"})
  @SqlAfter ({ "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedInParticipant() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"})
  @SqlAfter ({ "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedInOrganizer() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlSets ("illusion-event-oai")
  public void testJoinOpenNotLoggedIn() {
    navigate("/illusion/event/open");
    assertSelectorClickable(".illusion-event-join-button");
    clickSelector(".illusion-event-join-button");
    assertLogin();
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    assertUrlMatches(".*/illusion/event/open");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }

  @Test
  @SqlSets ("illusion-event-oai")
  public void testJoinApproveNotLoggedIn() throws MessagingException {
    GreenMail greenMail = startSmtpServer();
    try {
      navigate("/illusion/event/approve");
      assertSelectorClickable(".illusion-event-join-button");
      clickSelector(".illusion-event-join-button");
      assertLogin();
      loginInternal(getWebDriver(), "user@foyt.fi", "pass");
      assertUrlMatches(".*/illusion/event/approve.*");
      waitForNotification();
      assertNotification("info", "Your request to join the event was sent to event organizers for approval.");
      navigate("/illusion/event/approve");
      assertNotification("warning", "Waiting for event organizer to accept your request...");
      
      assertEquals(1, greenMail.getReceivedMessages().length);

      String mailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Request to join group", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(mailBody, StringUtils.startsWithIgnoreCase(mailBody, "Hi Test Admin"));
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ("illusion-event-oai")
  public void testJoinInviteNotLoggedIn() throws MessagingException {
    navigate("/illusion/event/invite");
    assertSelectorNotPresent(".illusion-event-join-button");
  }
  
  @Test
  @SqlSets ("illusion-event-oai")
  public void testJoinOpenLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion/event/open");
    assertSelectorClickable(".illusion-event-join-button");
    clickSelector(".illusion-event-join-button");
    assertUrlMatches(".*/illusion/event/open");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }

  @Test
  @SqlSets ("illusion-event-oai")
  public void testJoinApproveLoggedIn() throws MessagingException {
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      navigate("/illusion/event/approve");
      assertSelectorClickable(".illusion-event-join-button");
      clickSelector(".illusion-event-join-button");
      assertUrlMatches(".*/illusion/event/approve.*");
      waitForNotification();
      assertNotification("info", "Your request to join the event was sent to event organizers for approval.");
      navigate("/illusion/event/approve");
      assertNotification("warning", "Waiting for event organizer to accept your request...");
      
      assertEquals(1, greenMail.getReceivedMessages().length);

      String mailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Request to join group", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(mailBody, StringUtils.startsWithIgnoreCase(mailBody, "Hi Test Admin"));
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ("illusion-event-oai")
  public void testJoinInviteLoggedIn() throws MessagingException {
    navigate("/illusion/event/invite");
    loginInternal("user@foyt.fi", "pass");
    assertSelectorNotPresent(".illusion-event-join-button");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-banned-setup.sql"})
  @SqlAfter ({ "illusion-event-open-banned-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testBanned() throws MessagingException {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion/event/openevent");
    waitForNotification();
    assertNotification("warning", "You have been banned from this event");
    assertSelectorNotPresent(".illusion-event-join-button");
    navigate("/illusion/event/openevent/dojoin");
    assertAccessDenied();
  }

}
