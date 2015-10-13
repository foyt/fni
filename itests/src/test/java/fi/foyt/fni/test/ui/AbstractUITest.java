package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.AbstractTest;

public abstract class AbstractUITest extends AbstractTest {
  
  protected String stripLinkJSessionId(String link) {
    if (StringUtils.isNotBlank(link)) {
      link = link.replaceFirst(";jsessionid=[a-zA-Z0-9\\.\\-]*", "");
    }
    
    return link;
  }
  
  protected void testLoginRequired(RemoteWebDriver driver, String path) throws UnsupportedEncodingException {
    testLoginRequired(driver, path, false);  
  }
  
  protected void testLoginRequired(RemoteWebDriver driver, String path, boolean secure) throws UnsupportedEncodingException {
    String appUrl = getAppUrl(secure);
    String ctxPath = getCtxPath();
    driver.get(appUrl + path);
    String expectedUrl = getAppUrl(true) + "/login/?redirectUrl=" + URLEncoder.encode(ctxPath != null ? "/" + ctxPath + path : path, "UTF-8");
    waitForUrlMatches(driver, "https://.*");
    assertEquals(expectedUrl, driver.getCurrentUrl());
  }
  
  protected void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }
  
  protected void logout(RemoteWebDriver driver) {
    driver.get(getAppUrl() + "/logout");
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-login")).size());
  }

  protected void acceptCookieDirective(RemoteWebDriver driver) {
    acceptCookieDirective(driver, false);
  }
  
  protected void acceptCookieDirective(RemoteWebDriver driver, boolean secure) {
    driver.get(getAppUrl(secure) + "/");
    driver.manage().addCookie(new Cookie("cookiesDirective", "1", getHost(), "/", null));
  }
  
  protected void testAccessDenied(RemoteWebDriver driver, String view) {
    testAccessDenied(driver, view, false);
  }
  
  protected void testAccessDenied(RemoteWebDriver driver, String view, boolean secure) {
    driver.get(getAppUrl(secure) + view);
    assertEquals("Access Denied!", driver.getTitle());
  }
  
  protected void testNotFound(RemoteWebDriver driver, String view) {
    testNotFound(driver, view, false); 
  }
  
  protected void testNotFound(RemoteWebDriver driver, String view, boolean secure) {
    driver.get(getAppUrl(secure) + view);
    assertNotFound(driver);
  }

  protected void assertNotFound(RemoteWebDriver driver) {
    assertEquals("Page Not Found!", driver.getTitle());
  }
  
  protected void waitForUrl(RemoteWebDriver driver, final String url) {
    new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return url.equals(driver.getCurrentUrl());
      }
    });
  }

  protected void waitForUrlMatches(RemoteWebDriver driver, final String regex) {
    new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.getCurrentUrl().matches(regex);
      }
    });
  }

  protected void waitForUrlNotMatches(RemoteWebDriver driver, final String regex) {
    new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return !driver.getCurrentUrl().matches(regex);
      }
    });
  }

  protected void waitForNotification(RemoteWebDriver driver) {
    new WebDriverWait(driver, 60)
      .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".notifications .notification")));
  }

  protected void assertNotification(RemoteWebDriver driver, String serverity, String text) {
    assertEquals(StringUtils.lowerCase(text), StringUtils.lowerCase(driver.findElement(By.cssSelector(".notification-" + serverity)).getText()));
  }

  protected void assertNotificationStartsWith(RemoteWebDriver driver, String serverity, String text) {
    assertTrue(StringUtils.startsWithIgnoreCase(driver.findElement(By.cssSelector(".notification-" + serverity)).getText(), text));
  }
  
}