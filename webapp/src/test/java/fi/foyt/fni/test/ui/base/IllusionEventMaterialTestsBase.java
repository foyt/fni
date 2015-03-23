package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet(id = "event", before = { "illusion-event-open-setup.sql" }, after = { "illusion-event-open-teardown.sql"}),  
  @DefineSqlSet(id = "event-participant", before = {"illusion-event-open-participant-setup.sql" }, after = {"illusion-event-open-participant-teardown.sql"}),
  @DefineSqlSet(id = "event-unpublished", before = { "illusion-event-open-unpublished-setup.sql" }, after = { "illusion-event-open-unpublished-teardown.sql"}),
  @DefineSqlSet(id = "event-organizer", before = {"illusion-event-open-organizer-setup.sql" }, after = {"illusion-event-open-organizer-teardown.sql"}),
  @DefineSqlSet(id = "event-invited", before = {"illusion-event-open-invited-setup.sql" }, after = {"illusion-event-open-invited-teardown.sql"}),
  @DefineSqlSet(id = "event-materials", before = {"illusion-event-open-materials-setup.sql"}, after = {"illusion-event-open-materials-teardown.sql"}),
  @DefineSqlSet(id = "event-materials-participants", before = {"illusion-event-open-materials-to-participants-setup.sql" }, after = {"illusion-event-open-materials-to-participants-teardown.sql"}),
  @DefineSqlSet(id = "event-materials-visible", before = {"illusion-event-open-materials-visible-setup.sql"}, after = {"illusion-event-open-materials-visible-teardown.sql"}),
  @DefineSqlSet(id = "event-materials-public", before = {"illusion-event-open-materials-public-setup.sql"}, after = {"illusion-event-open-materials-public-teardown.sql"}),
  @DefineSqlSet(id = "event-custom", before = { "illusion-event-open-custom-setup.sql" }, after = {"illusion-event-open-custom-teardown.sql"})
})
public class IllusionEventMaterialTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-participant", "event-materials", "event-materials-participants"})
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/materials/document");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-participant", "event-materials", "event-materials-participants"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/materials/nothing");
    testNotFound("/illusion/event/noevent/materials/document");
    testNotFound("/illusion/event/noevent//materials/document");
    testNotFound("/illusion/event/noevent/*/materials/document");
    testNotFound("/illusion/event/1/materials/document");
    testNotFound("/illusion/event///materials/document");
    testNotFound("/illusion/event//*/materials/document");
    testNotFound("/illusion/event/~/materials/document");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-participant", "event-materials", "event-materials-participants"})
  public void testLoggedInParticipant() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/materials/document", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-material");    
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "materials");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-invited", "event-materials", "event-materials-participants", "event-materials-public"})
  public void testLoggedInInvited() throws Exception {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/materials/document", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-material");    
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "materials");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-organizer", "event-materials", "event-materials-participants"})
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/materials/document", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-material");
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "materials");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-organizer", "event-materials", "event-materials-participants", "event-custom"})
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/materials/document");
    testTitle("Illusion - Open Event");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-organizer", "event-materials", "event-materials-participants", "event-custom"})
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/materials/document");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Illusion - Open Event");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-organizer", "event-materials", "event-materials-participants", "event-custom"})
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/materials/document");
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
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-organizer", "event-materials", "event-materials-participants", "event-custom"})
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/materials/document");
    testTitle("Illusion - Open Event");

    assertEquals(getAppUrl(), findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1)").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3)").getAttribute("href"));
    assertEquals(getCustomEventUrl(), findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/materials", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(7)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/materials/document", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(9)").getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "event", "event-unpublished", "event-organizer", "event-materials", "event-materials-participants", "event-materials-visible", "event-custom"})
  public void testUnpublishedAccessDenied() throws UnsupportedEncodingException {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/materials/document");
    logout();
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/materials/document");
    testTitle("Illusion - Open Event");
  }
  
  
}
