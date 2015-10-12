package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "event-basic", 
      before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql" },
      after = { "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-organizer", 
      before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql", "illusion-event-open-templates-setup.sql" },
      after = { "illusion-event-open-templates-teardown.sql", "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-custom", 
    before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-custom-setup.sql", "illusion-event-open-organizer-setup.sql", "illusion-event-open-templates-setup.sql" },
    after = { "illusion-event-open-templates-teardown.sql", "illusion-event-open-organizer-teardown.sql", "illusion-event-open-custom-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-participant", 
    before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql", "illusion-event-open-templates-setup.sql" },
    after = { "illusion-event-open-templates-teardown.sql", "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  )
})
public class IllusionEventEditTemplateTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets ("event-basic")
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/edit-template?templateId=1");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testNotFound() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/edit-template/");
    testNotFound("/illusion/event/noevent/edit-template");
    testNotFound("/illusion/event/noevent//edit-template");
    testNotFound("/illusion/event/noevent/*/edit-template");
    testNotFound("/illusion/event/1/edit-template");
    testNotFound("/illusion/event///edit-template");
    testNotFound("/illusion/event//*/edit-template");
    testNotFound("/illusion/event/~/edit-template");
    testNotFound("/illusion/event/openevent/edit-template?templateId=123456");
    testNotFound("/illusion/event/openevent/edit-template?templateId=a");
    testNotFound("/illusion/event/openevent/edit-template?templateId=");
    testNotFound("/illusion/event/openevent/edit-template?templateId=*");
    testNotFound("/illusion/event/openevent/edit-template?templateId=~");
  }
  
  @Test
  @SqlSets ("event-basic")
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/edit-template?templateId=1");
  }
  
  @Test
  @SqlSets ("event-participant")
  public void testAccessDeniedParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/edit-template?templateId=1");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/edit-template?templateId=1", "Open Event - Edit Template dummy");
    assertSelectorCount(".illusion-event-navigation-admin-menu.illusion-event-navigation-item-active", 1);
    clickSelector(".illusion-event-navigation-admin-menu");
    waitForSelectorVisible(".illusion-event-navigation-admin-menu .illusion-event-navigation-menu-items");
    assertSelectorCount(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", "manage templates");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/edit-template?templateId=1");
    testTitle("Open Event - Edit Template dummy");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/edit-template?templateId=1");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Open Event - Edit Template dummy");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/edit-template?templateId=1");
    testTitle("Open Event - Edit Template dummy");
    assertMenuItems();
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/edit-template?templateId=1");
    testTitle("Open Event - Edit Template dummy");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1)").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/manage-templates", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(7)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/edit-template?templateId=1", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(9)").getAttribute("href"));
  }
  
}
