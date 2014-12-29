package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "event-basic", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql"},
    after = {"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql", "basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-organizer", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"},
    after = { "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql", "basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "event-custom", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-custom-setup.sql", "illusion-event-open-organizer-setup.sql" },
    after = { "illusion-event-open-organizer-teardown.sql", "illusion-event-open-custom-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql", "basic-users-teardown.sql"}
  )
})
public class IllusionEventSettingsTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets ("event-basic")
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/settings/");
    testNotFound("/illusion/event/noevent/settings");
    testNotFound("/illusion/event/noevent//settings");
    testNotFound("/illusion/event/noevent/*/settings");
    testNotFound("/illusion/event/1/settings");
    testNotFound("/illusion/event///settings");
    testNotFound("/illusion/event//*/settings");
    testNotFound("/illusion/event/~/settings");
  }
  
  @Test
  @SqlSets ("event-basic")
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testAccessDeniedParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/settings", "Event Settings");
    assertSelectorCount(".illusion-event-navigation-admin-menu.illusion-event-navigation-item-active", 1);
    clickSelector(".illusion-event-navigation-admin-menu");
    assertSelectorCount(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", "settings");
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testDates() throws Exception {
    acceptCookieDirective(getWebDriver());
    
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    String startDate = "10/20/2030";
    String startTime = "12:00";
    String endDate = "11/21/2031";
    String endTime = "10:30";
    
    assertSelectorValue("input[data-alt-field='.actual-start-date']", "01/01/2010");
    assertSelectorValue("input[data-alt-field='.actual-start-time']", "");
    assertSelectorValue("input[data-alt-field='.actual-end-date']", "");
    assertSelectorValue("input[data-alt-field='.actual-end-time']", "");
    
    clearSelectorInput("input[data-alt-field='.actual-start-date']");
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    typeSelectorInputValue("input[data-alt-field='.actual-start-time']", startTime);
    typeSelectorInputValue("input[data-alt-field='.actual-end-date']", endDate);
    typeSelectorInputValue("input[data-alt-field='.actual-end-time']", endTime);
    
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    assertSelectorValue("input[data-alt-field='.actual-start-time']", startTime);
    assertSelectorValue("input[data-alt-field='.actual-end-date']", endDate);
    assertSelectorValue("input[data-alt-field='.actual-end-time']", endTime);
    
    waitSelectorToBeClickable(".illusion-event-settings-save");
    clickSelector(".illusion-event-settings-save");
    
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    assertSelectorValue("input[data-alt-field='.actual-start-time']", startTime);
    assertSelectorValue("input[data-alt-field='.actual-end-date']", endDate);
    assertSelectorValue("input[data-alt-field='.actual-end-time']", endTime);
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testLocation() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String location = "Test place";
    typeSelectorInputValue(".illusion-event-settings-location", location);
    clickSelector(".illusion-event-settings-save");
    assertSelectorValue(".illusion-event-settings-location", location);
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/settings");
    testTitle("Event Settings");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/settings");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Event Settings");
  }
  
  @Test
  @SqlSets ("event-custom")
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/settings");
    testTitle("Event Settings");

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
  @SqlSets ("event-custom")
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/settings");
    testTitle("Event Settings");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1)").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/settings", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5)").getAttribute("href"));
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testDomain() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String location = "Test place";
    typeSelectorInputValue(".illusion-event-settings-domain", location);
    clickSelector(".illusion-event-settings-save");
    assertSelectorValue(".illusion-event-settings-domain", location);
    getWebDriver().get(getCustomEventUrl());
    testTitle("Illusion - Open Event");
  }

  @Test
  @SqlSets ("event-organizer")
  public void testEventSignUpDates() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String signUpStartDate = "10/05/2030";
    String signUpEndDate = "10/10/2030";
    
    typeSelectorInputValue(".sign-up-start-date", signUpStartDate);
    typeSelectorInputValue(".sign-up-end-date", signUpEndDate);
    clickSelector(".illusion-event-settings-save");
    navigate("/illusion/event/openevent/settings");
    assertSelectorValue(".sign-up-start-date", signUpStartDate);
    assertSelectorValue(".sign-up-end-date", signUpEndDate);
  }
  
  @Test
  @SqlSets ("event-organizer")
  public void testEventImageUrl() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String imageUrl = "http://www.url.to/image.png";

    typeSelectorInputValue(".illusion-event-settings-image-url", imageUrl);
    clickSelector(".illusion-event-settings-save");
    navigate("/illusion/event/openevent/settings");
    assertSelectorValue(".illusion-event-settings-image-url", imageUrl);
  }

  @Test
  @SqlSets ("event-organizer")
  public void testEventBeginnerFriendly() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-event-settings-beginner-friendly");
    clickSelector(".illusion-event-settings-save");
    navigate("/illusion/event/openevent/settings");
    assertSelectorPresent(".illusion-event-settings-beginner-friendly:checked");
  }

  @Test
  @SqlSets ("event-organizer")
  public void testEventAgeLimit() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String ageLimit = "16";

    typeSelectorInputValue(".illusion-event-settings-age-limit", ageLimit);
    clickSelector(".illusion-event-settings-save");
    navigate("/illusion/event/openevent/settings");
    assertSelectorValue(".illusion-event-settings-age-limit", ageLimit);
  }

  @Test
  @SqlSets ("event-organizer")
  public void testEventType() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    selectSelectBoxByValue(".illusion-event-settings-type", "2");
    clickSelector(".illusion-event-settings-save");
    navigate("/illusion/event/openevent/settings");
    assertSelectBoxValue(".illusion-event-settings-type", "2");
  }

  @Test
  @SqlSets ("event-organizer")
  public void testEventGenres() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    clickSelector(".illusion-event-settings-genre input[value='1']");
    clickSelector(".illusion-event-settings-genre input[value='3']");
    clickSelector(".illusion-event-settings-save");
    navigate("/illusion/event/openevent/settings");
    assertSelectorPresent(".illusion-event-settings-genre input[value='1']:checked");
    assertSelectorNotPresent(".illusion-event-settings-genre input[value='2']:checked");
    assertSelectorPresent(".illusion-event-settings-genre input[value='3']:checked");
    assertSelectorNotPresent(".illusion-event-settings-genre input[value='4']:checked");
  }
}
