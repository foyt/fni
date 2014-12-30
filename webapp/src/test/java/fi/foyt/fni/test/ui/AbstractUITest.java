package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.AbstractTest;

public abstract class AbstractUITest extends AbstractTest {
  
  @After
  public void flushCache() throws ClientProtocolException, IOException {
    HttpGet get = new HttpGet(getAppUrl() + "/rest/system/jpa/cache/flush");
    DefaultHttpClient client = new DefaultHttpClient();
    try {
      get.addHeader("Authorization", "Bearer systemtoken");
      HttpResponse response = client.execute(get);
      assertEquals(200, response.getStatusLine().getStatusCode());
    } finally {
      client.getConnectionManager().shutdown();
    }
  }
  
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
    String expectedUrl = getAppUrl(true) + "/login/?redirectUrl=" + URLEncoder.encode("/" + ctxPath + path, "UTF-8");
    waitForUrlMatches(driver, "https://.*");
    assertEquals(expectedUrl, driver.getCurrentUrl());
  }
  
  protected void loginInternal(RemoteWebDriver driver, String email, String password) {
    String loginUrl = getAppUrl(true) + "/login/";
    if (!StringUtils.startsWith(driver.getCurrentUrl(), loginUrl)) {
      driver.get(loginUrl);
    }
    
    driver.findElement(By.cssSelector(".user-login-email")).sendKeys(email);
    driver.findElement(By.cssSelector(".user-login-password")).sendKeys(password);
    driver.findElement(By.cssSelector(".user-login-button")).click();
    waitForUrlNotMatches(driver, ".*/login.*");

    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
  }

  protected void loginFacebook(RemoteWebDriver driver) {
    acceptCookieDirective(driver);
    driver.get(getAppUrl(true) + "/login/");
    driver.findElement(By.cssSelector(".user-login-external-facebook")).click();
    driver.findElement(By.id("email")).sendKeys(getFacebookUsername());
    driver.findElement(By.id("pass")).sendKeys(getFacebookPassword());
    driver.findElement(By.name("login")).click();
    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
  }

  protected void loginGoogle(RemoteWebDriver driver) {
    acceptCookieDirective(driver);
    driver.get(getAppUrl(true) + "/login/");
    driver.findElement(By.cssSelector(".user-login-external-google")).click();
    sleep(500);
    driver.findElement(By.name("Email")).sendKeys(getGoogleUsername());
    driver.findElement(By.name("Passwd")).sendKeys(getGooglePassword());
    driver.findElement(By.name("signIn")).click();
    
    waitForUrlMatches(driver, "^" + getAppUrl() + ".*");
    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
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
  
  protected void testTitle(RemoteWebDriver driver, String view, String expectedTitle) {
    testTitle(driver, view, expectedTitle, false);
  }

  protected void testTitle(RemoteWebDriver driver, String view, String expectedTitle, boolean secure) {
    driver.get(getAppUrl(secure) + view);
    assertEquals(expectedTitle, driver.getTitle());
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