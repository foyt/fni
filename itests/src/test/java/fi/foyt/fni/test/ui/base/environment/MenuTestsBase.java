package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fi.foyt.fni.test.ui.base.AbstractUITest;

public class MenuTestsBase extends AbstractUITest {

  @Test
  public void testItems() {
    navigate("/");
    assertSelectorVisible(String.format("a[href='/']"));
    assertSelectorVisible(String.format("a[href='/forge/']"));
    assertSelectorVisible(String.format("a[href='/illusion/']"));
    assertSelectorVisible(String.format("a[href='/gamelibrary/']"));
    assertSelectorVisible(String.format("a[href='/forum/']"));
    assertSelectorTextIgnoreCase(String.format("a[href='/forge/']"), "Forge");
    assertSelectorTextIgnoreCase(String.format("a[href='/illusion/']"), "Illusion");
    assertSelectorTextIgnoreCase(String.format("a[href='/gamelibrary/']"), "Game Library");
    assertSelectorTextIgnoreCase(String.format("a[href='/forum/']"), "Forum");
  }

  @Test
  public void testAbout() throws Exception {navigate("/");

    WebElement aboutMenuLink = findElementBySelector("a.menu-about");
    WebElement aboutMenuList = findElementBySelector(".menu-about-list");

    assertEquals("About", aboutMenuLink.getText());

    // Menu list should be hidden by default
    assertEquals(false, aboutMenuList.isDisplayed());

    // Check about menu
    aboutMenuLink.click();

    assertEquals(true, aboutMenuList.isDisplayed());

    WebElement aboutMenuVision = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(1)>a"));
    WebElement aboutMenuInformation = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(2)>a"));
    WebElement aboutMenuForum = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(3)>a"));
    WebElement aboutMenuDistribution = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(4)>a"));
    WebElement aboutMenuGameplay = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(5)>a"));
    WebElement aboutMenuHistory = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(6)>a"));
    WebElement aboutMenuCookies = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(7)>a"));
    WebElement aboutMenuOpenSource = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(8)>a"));
    WebElement aboutMenuContact = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(9)>a"));
    WebElement aboutMenuAcknowledgements = getWebDriver().findElement(By.cssSelector(".menu-about-list>li:nth-child(10)>a"));

    assertEquals("Our Vision", aboutMenuVision.getText());
    assertEquals(getAppUrl() + "/about#vision", stripLinkJSessionId(aboutMenuVision.getAttribute("href")));

    assertEquals("Information", aboutMenuInformation.getText());
    assertEquals(getAppUrl() + "/about#information", stripLinkJSessionId(aboutMenuInformation.getAttribute("href")));

    assertEquals("Community participation and forum", aboutMenuForum.getText());
    assertEquals(getAppUrl() + "/about#forum", stripLinkJSessionId(aboutMenuForum.getAttribute("href")));

    assertEquals("Distribution", aboutMenuDistribution.getText());
    assertEquals(getAppUrl() + "/about#distribution", stripLinkJSessionId(aboutMenuDistribution.getAttribute("href")));

    assertEquals("Gameplay", aboutMenuGameplay.getText());
    assertEquals(getAppUrl() + "/about#gameplay", stripLinkJSessionId(aboutMenuGameplay.getAttribute("href")));

    assertEquals("History", aboutMenuHistory.getText());
    assertEquals(getAppUrl() + "/about#history", stripLinkJSessionId(aboutMenuHistory.getAttribute("href")));

    assertEquals("Use of cookies", aboutMenuCookies.getText());
    assertEquals(getAppUrl() + "/about#cookies", stripLinkJSessionId(aboutMenuCookies.getAttribute("href")));

    assertEquals("Open Source", aboutMenuOpenSource.getText());
    assertEquals(getAppUrl() + "/about#opensource", stripLinkJSessionId(aboutMenuOpenSource.getAttribute("href")));

    assertEquals("Contacting us", aboutMenuContact.getText());
    assertEquals(getAppUrl() + "/about#contact", stripLinkJSessionId(aboutMenuContact.getAttribute("href")));

    assertEquals("Acknowledgements", aboutMenuAcknowledgements.getText());
    assertEquals(getAppUrl() + "/about#acknowledgements", stripLinkJSessionId(aboutMenuAcknowledgements.getAttribute("href")));

    // Click somewhere else and the menu list should disappear
    clickSelector(".menu-tools-locale-container");
    assertEquals(false, aboutMenuList.isDisplayed());

    // Click link again and the menu list should reappear
    aboutMenuLink.click();
    assertEquals(true, aboutMenuList.isDisplayed());

    // ... and stay visible after another click
    aboutMenuLink.click();
    assertEquals(true, aboutMenuList.isDisplayed());
  }

  @Test
  public void testLocaleMenu() {
    getWebDriver().get(getAppUrl());

    WebElement localeMenuList = getWebDriver().findElement(By.cssSelector(".menu-tools-locale-list"));

    assertSelectorText(".index-menu .menu-tools-container .menu-tools-locale", "EN", true, true);

    // Menu list should be hidden by default
    assertEquals(false, localeMenuList.isDisplayed());

    // Click menu should make the list appear
    clickSelector(".index-menu .menu-tools-container .menu-tools-locale");
    assertEquals(true, localeMenuList.isDisplayed());

    WebElement fiItem = getWebDriver().findElement(By.cssSelector(".menu-tools-locale-list>li:nth-child(1)>a"));
    WebElement enItem = getWebDriver().findElement(By.cssSelector(".menu-tools-locale-list>li:nth-child(2)>a"));

    assertEquals("Suomi", fiItem.getText());
    assertEquals("English", enItem.getText());

    // Click somewhere else and the menu list should disappear
    clickSelector(".menu-about-container");
    assertEquals(false, localeMenuList.isDisplayed());

    // Click link again and the menu list should reappear
    clickSelector(".index-menu .menu-tools-container .menu-tools-locale");
    assertEquals(true, localeMenuList.isDisplayed());

    // ... and stay visible after another click
    clickSelector(".index-menu .menu-tools-container .menu-tools-locale");
    assertEquals(true, localeMenuList.isDisplayed());
  }

}
