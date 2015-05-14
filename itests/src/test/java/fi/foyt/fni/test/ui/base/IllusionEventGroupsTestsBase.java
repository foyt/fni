package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "illusion-event-basic", 
    before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql"},
    after = { "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql" }
  ),
  @DefineSqlSet (id = "illusion-event-participant", 
    before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql"},
    after = { "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql" }
  ),
    @DefineSqlSet (id = "illusion-event-organizer", 
    before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"},
    after = { "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql" }
  ),
  @DefineSqlSet (id = "illusion-event-custom", 
    before = { "basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-custom-setup.sql", "illusion-event-open-organizer-setup.sql" },
    after = { "illusion-event-open-organizer-teardown.sql", "illusion-event-open-custom-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql" }
  )
})
public class IllusionEventGroupsTestsBase extends AbstractIllusionUITest {

  @Test
  @SqlSets ("illusion-event-basic")
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/groups");
  }
  
  @Test
  @SqlSets ("illusion-event-basic")
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/groups/");
    testNotFound("/illusion/event/noevent/groups");
    testNotFound("/illusion/event/noevent//groups");
    testNotFound("/illusion/event/noevent/*/groups");
    testNotFound("/illusion/event/1/groups");
    testNotFound("/illusion/event///groups");
    testNotFound("/illusion/event//*/groups");
    testNotFound("/illusion/event/~/groups");
  }
  
  @Test
  @SqlSets ("illusion-event-basic")
  public void testLoggedIn() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/groups");
  }
  
  @Test
  @SqlSets ("illusion-event-participant")
  public void testLoggedInParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/groups");
  }
  
  @Test
  @SqlSets ("illusion-event-organizer")
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/groups", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 1);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
  }
  
  @Test
  @SqlSets ("illusion-event-custom")
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/groups");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets ("illusion-event-custom")
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/groups");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets ("illusion-event-custom")
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/groups");
    testTitle("Illusion - Open Event");
    assertMenuItems();
  }
  
  @Test
  @SqlSets ("illusion-event-custom")
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/groups");
    testTitle("Illusion - Open Event");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1) a").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3) a").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5) a").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/groups", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(7) a").getAttribute("href"));
  }

}
