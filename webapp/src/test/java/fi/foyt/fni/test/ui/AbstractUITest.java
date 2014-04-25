package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

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
    assertEquals(appUrl + "/login?redirectUrl=" + URLEncoder.encode("/" + ctxPath + path, "UTF-8"), driver.getCurrentUrl());
  }
  
  protected void loginInternal(RemoteWebDriver driver, String email, String password) {
    String loginUrl = getAppUrl() + "/login";
    if (!StringUtils.startsWith(driver.getCurrentUrl(), loginUrl)) {
      driver.get(loginUrl);
    }
    
    driver.findElement(By.cssSelector(".user-login-login-panel input[type='text']"))
      .sendKeys(email);
    driver.findElement(By.cssSelector(".user-login-login-panel input[type='password']"))
      .sendKeys(password);
    driver.findElement(By.cssSelector(".user-login-login-panel input[type='submit']"))
      .click();
  }
    
  protected void testTitle(ChromeDriver driver, String view, String expectedTitle) {
    testTitle(driver, view, expectedTitle, false);
  }

  protected void testTitle(ChromeDriver driver, String view, String expectedTitle, boolean secure) {
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
    assertEquals("Page Not Found!", driver.getTitle());
  }
  
}