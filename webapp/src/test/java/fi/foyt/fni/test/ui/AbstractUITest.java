package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
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

    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
  }

  protected void loginFacebook(ChromeDriver driver) {
    driver.get(getAppUrl() + "/login");
    driver.findElement(By.cssSelector("a[href=\"?loginMethod=FACEBOOK\"]")).click();
    driver.findElement(By.id("email")).sendKeys(getFacebookUsername());
    driver.findElement(By.id("pass")).sendKeys(getFacebookPassword());
    driver.findElement(By.name("login")).click();
    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
  }

  protected void loginGoogle(ChromeDriver driver) {
    driver.get(getAppUrl() + "/login");
    driver.findElement(By.cssSelector("a[href=\"?loginMethod=GOOGLE\"]")).click();
    driver.findElement(By.name("Email")).sendKeys(getGoogleUsername());
    driver.findElement(By.name("Passwd")).sendKeys(getGooglePassword());
    driver.findElement(By.name("signIn")).click();
    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
  }
  
  protected void logout(RemoteWebDriver driver) {
    driver.get(getAppUrl() + "/logout");
    assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-account")).size());
    assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-login")).size());
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

  protected void waitForUrl(ChromeDriver driver, final String url) {
    new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return url.equals(driver.getCurrentUrl());
      }
    });
  }

  protected void waitForUrlMatches(ChromeDriver driver, final String regex) {
    new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.getCurrentUrl().matches(regex);
      }
    });
  }
  
}