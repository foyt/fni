package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
  
  protected void logout() {
    logout(getWebDriver());
  }

  protected void assertLoggedIn() {
    assertSelectorNotVisible(".index-menu .menu-tools-login");
    assertSelectorVisible(".index-menu .menu-tools-account-container");
  }
  
  protected void assertNotLoggedIn() {
    assertSelectorVisible(".index-menu .menu-tools-login");
    assertSelectorNotVisible(".index-menu .menu-tools-account-container");
  }
  
  protected void waitSelectorToBeClickable(String selector) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.elementToBeClickable(findElementBySelector(selector)));
  }
  
  protected void waitForElementVisible(WebElement element) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOf(element));
  }

  protected void waitForSelectorVisible(String selector) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(selector)));
  }

  protected void waitForSelectorNotPresent(final String selector) {
    
    new WebDriverWait(getWebDriver(), 60).until(
      new ExpectedCondition<List<WebElement>>() {
        
        @Override
        public List<WebElement> apply(WebDriver driver) {
          List<WebElement> elements = driver.findElements(By.cssSelector(selector));
          return elements.size() > 0 ? elements : null;
        }
        
      }
    );
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
  
  protected void waitForSelectorText(final String selector, final String text) {
    waitForSelectorText(selector, text, false);
  }
  
  protected void waitForSelectorText(final String selector, final String text, final boolean ignoreCase) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        if (ignoreCase) {
          return text.equalsIgnoreCase(driver.findElement(By.cssSelector(selector)).getText());
        } else {
          return text.equals(driver.findElement(By.cssSelector(selector)).getText());
        }
      }
    }); 
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
  
  protected void assertNotificationStartsWith(String serverity, String text) {
    assertNotificationStartsWith(getWebDriver(), serverity, text);
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

  protected void assertSelectorVisible(String selector) {
    assertTrue("Element visible '" + selector + "'", getWebDriver().findElementByCssSelector(selector).isDisplayed());
  }

  protected void assertSelectorNotVisible(String selector) {
    if (getWebDriver().findElementsByCssSelector(selector).size() == 0) {
      return;
    }
    
    assertTrue("Element not visible '" + selector + "'", !getWebDriver().findElementByCssSelector(selector).isDisplayed());
  }
  
  protected void assertSelectorClickable(String selector) {
    assertNotNull(ExpectedConditions.elementToBeClickable(findElementBySelector(selector)).apply(getWebDriver()));
  }
  
  protected void assertSelectorCount(String selector, int expected) {
    assertEquals(expected, getWebDriver().findElementsByCssSelector(selector).size());
  }

  protected void assertSelectorValue(String selector, String expected) {
    assertEquals(expected, findElementBySelector(selector).getAttribute("value"));
  }

  protected void assertTitle(String expected) {
    assertEquals(expected, getWebDriver().getTitle());
  }
  
  protected void assertAccessDenied() {
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  protected void assertLogin() {
    assertUrlMatches(".*/login.*");
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
  
  protected void clearSelectorInput(String selector) {
    findElementBySelector(selector).clear();
  }
  
  protected void typeSelectorInputValue(String selector, String value) {
    findElementBySelector(selector).sendKeys(value);
  }

  protected void assertNotFound() {
    assertEquals("Page Not Found!", getWebDriver().getTitle());
  }
  
  protected void acceptCookieDirective() {
    acceptCookieDirective(getWebDriver()); 
  }
  
  protected void selectSelectBoxByValue(String selector, String value) {
    new Select(findElementBySelector(selector)).selectByValue(value);
  }
  
  protected void assertSelectBoxValue(String selector, String value) {
    WebElement selectedOption = new Select(findElementBySelector(selector)).getFirstSelectedOption();
    assertNotNull(selectedOption);
    assertEquals(value, selectedOption.getAttribute("value"));
  }

  protected void scrollWindowBy(int x, int y) {
    ((JavascriptExecutor) getWebDriver()).executeScript(String.format("window.scrollBy(%d, %d)",  x, y), "");
  }

  protected void switchFrame(String selector) {
    getWebDriver().switchTo().frame(findElementBySelector(selector));
  }
  
  protected void switchDefault() {
    getWebDriver().switchTo().defaultContent();
  }
  
  protected void takeScreenshot() throws WebDriverException, IOException {
    takeScreenshot(new File("target", UUID.randomUUID().toString() + ".png"));
  }
  
  protected void takeScreenshot(File file) throws WebDriverException, IOException {
    FileOutputStream fileOuputStream = new FileOutputStream(file);
    try {
     fileOuputStream.write(webDriver.getScreenshotAs(OutputType.BYTES));
    } finally {
      fileOuputStream.flush();
      fileOuputStream.close();
    }
  }

  private String sessionId;
  private RemoteWebDriver webDriver;
}
