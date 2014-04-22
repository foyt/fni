package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import fi.foyt.fni.test.AbstractTest;

public abstract class AbstractUITest extends AbstractTest {
  
  protected void testLoginRequired(RemoteWebDriver driver, String path) throws UnsupportedEncodingException {
    String appUrl = getAppUrl();
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
  
  protected void testAccessDenied(RemoteWebDriver driver, String view) {
    driver.get(getAppUrl() + view);
    assertEquals("Access Denied!", driver.getTitle());
  }
  
}