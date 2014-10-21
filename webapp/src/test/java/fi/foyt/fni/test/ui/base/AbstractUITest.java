package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.saucelabs.common.SauceOnDemandSessionIdProvider;

public class AbstractUITest extends fi.foyt.fni.test.ui.AbstractUITest implements SauceOnDemandSessionIdProvider {

  @Override
  public String getSessionId() {
    return sessionId;
  }
  
  protected void setWebDriver(RemoteWebDriver webDriver) {
    this.webDriver = webDriver;
    this.sessionId = webDriver.getSessionId().toString();
  }
  
  protected RemoteWebDriver getWebDriver() {
    return webDriver;
  }
  
  protected RemoteWebDriver createSauceWebDriver(String browser, String version, String platform) throws MalformedURLException {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    
    capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
    capabilities.setCapability(CapabilityType.VERSION, version);
    capabilities.setCapability(CapabilityType.PLATFORM, platform);
    capabilities.setCapability("name", getClass().getSimpleName() + ':' + testName.getMethodName());
    capabilities.setCapability("tags", Arrays.asList( String.valueOf( getTestStartTime() ) ) );
    capabilities.setCapability("build", getProjectVersion());
    capabilities.setCapability("video-upload-on-pass", false);
    capabilities.setCapability("capture-html", true);
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
    capabilities.setCapability("selenium-version", "2.42.0");

    return new RemoteWebDriver(new URL(String.format("http://%s:%s@%s:%s/wd/hub", getSauceUsername(), getSauceAccessKey(), getSauceHost(), getSaucePort())), capabilities);
  }
  
  protected void loginInternal(String email, String password) {
    loginInternal(getWebDriver(), email, password);
  }
  
  protected void waitSelectorToBeClickable(String selector) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.elementToBeClickable(findElementBySelector(selector)));
  }
  
  protected void waitForElementVisible(WebElement element) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOf(element));
  }
  
  protected WebElement findElementBySelector(String selector) {
    return getWebDriver().findElementByCssSelector(selector);
  }

  protected void assertSelectorTextIgnoreCase(String selector, String text) {
    assertEquals(StringUtils.lowerCase(text), StringUtils.lowerCase((findElementBySelector(selector)).getText()));
  }

  protected void waitForUrlMatches(String regex) {
    waitForUrlMatches(getWebDriver(), regex);
  }

  protected void waitForUrlNotMatches(String regex) {
    waitForUrlNotMatches(getWebDriver(), regex);
  }
  
  protected void assertUrlMatches(String regex) {
    assertTrue("url '" + getWebDriver().getCurrentUrl() + "' does not match " + regex, getWebDriver().getCurrentUrl().matches(regex));
  }

  protected void waitForNotification() {
    waitForNotification(getWebDriver());
  }

  protected void assertNotification(String serverity, String text) {
    assertNotification(getWebDriver(), serverity, text);
  }
  
  protected void navigate(String path) {
    navigate(path, false);
  }
  
  protected void navigate(String path, Boolean secure) {
    getWebDriver().get(getAppUrl(secure) + path);
  }
  
  protected void testLoginRequired(String path) throws UnsupportedEncodingException {
    testLoginRequired(getWebDriver(), path);
  }
  
  protected void testAccessDenied(String path) throws UnsupportedEncodingException {
    testAccessDenied(getWebDriver(), path);
  }
  
  protected void testTitle(String view, String expectedTitle) {
    testTitle(getWebDriver(), view, expectedTitle);
  }

  protected void testTitle(String expected) {
    assertEquals(expected, getWebDriver().getTitle());
  }
  
  protected void assertSelectorPresent(String selector) {
    assertTrue("Element not present '" + selector + "'", getWebDriver().findElementsByCssSelector(selector).size() == 1);
  }

  protected void assertSelectorNotPresent(String selector) {
    assertTrue("Element present '" + selector + "'", getWebDriver().findElementsByCssSelector(selector).size() == 0);
  }
  
  protected void assertSelectorCount(String selector, int expected) {
    assertEquals(expected, getWebDriver().findElementsByCssSelector(selector).size());
  }
  
  protected void clickSelector(String selector) {
    getWebDriver().findElementByCssSelector(selector).click();
  }
  
  protected void sendKeysSelector(String selector, String keysToSend) {
    getWebDriver().findElementByCssSelector(selector).sendKeys(keysToSend);
  }

  protected void testNotFound(String path) {
    testNotFound(getWebDriver(), path);
  }
  
  private String sessionId;
  
  private RemoteWebDriver webDriver;
}
