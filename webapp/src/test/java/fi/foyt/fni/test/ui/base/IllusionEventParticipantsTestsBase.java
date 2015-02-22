package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "illusion-basic", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql"},
    after = {"illusion-basic-teardown.sql", "basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "illusion-event", 
    before = {"illusion-event-open-setup.sql"},
    after = {"illusion-event-open-teardown.sql"}
  ),
  @DefineSqlSet (id = "illusion-event-organizer", 
    before = {"illusion-event-open-organizer-setup.sql"},
    after = {"illusion-event-open-organizer-teardown.sql"}
  ),
  @DefineSqlSet (id = "illusion-event-participant", 
  before = {"illusion-event-open-participant-setup.sql"},
  after = {"illusion-event-open-participant-teardown.sql"}
),
  @DefineSqlSet (id = "illusion-event-custom", 
    before = {"illusion-event-open-custom-setup.sql"},
    after = {"illusion-event-open-custom-teardown.sql"}
  )
})
public class IllusionEventParticipantsTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event"})
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/participants");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/participants/");
    testNotFound("/illusion/event/noevent/participants");
    testNotFound("/illusion/event/noevent//participants");
    testNotFound("/illusion/event/noevent/*/participants");
    testNotFound("/illusion/event/1/participants");
    testNotFound("/illusion/event///participants");
    testNotFound("/illusion/event//*/participants");
    testNotFound("/illusion/event/~/participants");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event"})
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/participants");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-participant"})
  public void testAccessDeniedParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/participants");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/participants", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation-admin-menu.illusion-event-navigation-item-active", 1);
    clickSelector(".illusion-event-navigation-admin-menu");
    assertSelectorCount(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", "participants");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/participants");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/participants");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/participants");
    testTitle("Illusion - Open Event");

    WebElement logoLink = getWebDriver().findElement(By.cssSelector(".index-menu>a:first-child"));
    WebElement forgeMenuLink = getWebDriver().findElement(By.cssSelector(".index-menu .menu-navigation-container>a:nth-child(1)"));
    WebElement illusionMenuLink = getWebDriver().findElement(By.cssSelector(".index-menu .menu-navigation-container>a:nth-child(2)"));
    WebElement gameLibraryMenuLink = getWebDriver().findElement(By.cssSelector(".index-menu .menu-navigation-container>a:nth-child(3)"));
    WebElement forumMenuLink = getWebDriver().findElement(By.cssSelector(".index-menu .menu-navigation-container>a:nth-child(4)"));

    assertEquals("Forge", forgeMenuLink.getText());
    assertEquals("Illusion", illusionMenuLink.getText());
    assertEquals("Game Library", gameLibraryMenuLink.getText());
    assertEquals("Forum", forumMenuLink.getText());

    assertEquals(getAppUrl() + "/", stripLinkJSessionId(logoLink.getAttribute("href")));
    assertEquals(getAppUrl() + "/forge", stripLinkJSessionId(forgeMenuLink.getAttribute("href")));
    assertEquals(getAppUrl() + "/illusion", stripLinkJSessionId(illusionMenuLink.getAttribute("href")));
    assertEquals(getAppUrl() + "/gamelibrary", stripLinkJSessionId(gameLibraryMenuLink.getAttribute("href")));
    assertEquals(getAppUrl() + "/forum", stripLinkJSessionId(forumMenuLink.getAttribute("href")));
  }
  
  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/participants");
    testTitle("Illusion - Open Event");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1) a").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3) a").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5) a").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/participants", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(7) a").getAttribute("href"));
  }

  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-participant", "illusion-event-organizer"})
  public void testUpdateRole() {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/participants");
    clickSelector(".illusion-event-participant[data-participant-id='1']");
    assertSelectorValue(".illusion-edit-participant-role", "PARTICIPANT");
    selectSelectBoxByValue(".illusion-edit-participant-role", "BANNED");
    clickSelector(".illusion-edit-participant-save");
    clickSelector(".illusion-event-participant[data-participant-id='1']");
    assertSelectorValue(".illusion-edit-participant-role", "BANNED");
  }

  @Test
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-participant", "illusion-event-organizer"})
  public void testUpdateDisplayName() {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/participants");
    clickSelector(".illusion-event-participant[data-participant-id='1']");
    assertSelectorValue(".illusion-edit-participant-display-name", "");
    typeSelectorInputValue(".illusion-edit-participant-display-name", "Display Name");
    clickSelector(".illusion-edit-participant-save");
    clickSelector(".illusion-event-participant[data-participant-id='1']");
    assertSelectorValue(".illusion-edit-participant-display-name", "Display Name");
  }
  
}
