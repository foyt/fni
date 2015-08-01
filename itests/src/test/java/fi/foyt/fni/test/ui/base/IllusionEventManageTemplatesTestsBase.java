package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "illusion-basic", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql"}, 
    after = {"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-organizer", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"},
    after = { "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-custom", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-custom-setup.sql", "illusion-event-open-organizer-setup.sql" },
    after = { "illusion-event-open-organizer-teardown.sql", "illusion-event-open-custom-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  )
})
public class IllusionEventManageTemplatesTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets ("illusion-basic")
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/manage-templates");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/manage-templates/");
    testNotFound("/illusion/event/noevent/manage-templates");
    testNotFound("/illusion/event/noevent//manage-templates");
    testNotFound("/illusion/event/noevent/*/manage-templates");
    testNotFound("/illusion/event/1/manage-templates");
    testNotFound("/illusion/event///manage-templates");
    testNotFound("/illusion/event//*/manage-templates");
    testNotFound("/illusion/event/~/manage-templates");
  }
  
  @Test
  @SqlSets ("illusion-basic")
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/manage-templates");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testAccessDeniedParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/manage-templates");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/manage-templates", "Open Event - Manage Templates");
    assertSelectorCount(".illusion-event-navigation-admin-menu.illusion-event-navigation-item-active", 1);
    clickSelector(".illusion-event-navigation-admin-menu");
    assertSelectorCount(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", "manage templates");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/manage-templates");
    testTitle("Open Event - Manage Templates");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/manage-templates");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Open Event - Manage Templates");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/manage-templates");
    testTitle("Open Event - Manage Templates");
    assertMenuItems();
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/manage-templates");
    testTitle("Open Event - Manage Templates");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1)").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/manage-templates", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(7)").getAttribute("href"));
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testIndexTemplate() throws Exception {
    acceptCookieDirective();
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/manage-templates");
    clickSelector(".new-template");
    waitForSelectorVisible(".CodeMirror-code");
    clearSelectorInput(".illusion-event-template-name");
    typeSelectorInputValue(".illusion-event-template-name", "index-contents");
    executeScript("$('textarea.illusion-event-template-editor').codeMirror('value', 'h1(id=\"test-header\")|header')");
    clickSelector(".illusion-event-template-save");
    waitForPageLoad();
    navigate("/illusion/event/openevent");
    waitForSelectorPresent("#test-header");
    assertSelectorPresent("#test-header");
    deleteIllusionTemplate("openevent", "index-contents");
  }

}
