package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
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
    capabilities.setCapability("timeZone", "Universal");
    capabilities.setCapability("seleniumVersion", getSeleniumVersion());
    
    if (getSauceTunnelId() != null) {
      capabilities.setCapability("tunnel-identifier", getSauceTunnelId());
    }
    
    return new RemoteWebDriver(new URL(String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", getSauceUsername(), getSauceAccessKey())), capabilities);
  }
  
  protected void loginInternal(String email, String password) {
    loginInternal(getWebDriver(), email, password);
  }
  
  protected void loginInternal(RemoteWebDriver driver, String email, String password) {
    String loginUrl = getAppUrl(true) + "/login/";
    if (!StringUtils.startsWith(driver.getCurrentUrl(), loginUrl)) {
      driver.get(loginUrl);
    } 
    
    if (driver.manage().getCookieNamed("cookiesDirective") == null) {
      driver.manage().addCookie(new Cookie("cookiesDirective", "1", getHost(), "/", null));
      driver.get(loginUrl);
    }
    
    waitAndSendKeys(".user-login-email", email);
    waitAndSendKeys(".user-login-password", password);
    waitAndClick(".user-login-button");
    waitForUrlNotMatches(driver, ".*/login.*");
    waitForSelectorPresent(".menu-tools-account");

    assertSelectorPresent(".menu-tools-account");
    assertSelectorNotPresent(".menu-tools-login");
  }

  protected void loginFacebook() {
    acceptCookieDirective();
    navigate("/login/", true);
    waitForSelectorVisible(".user-login-external-facebook");
    clickSelector(".user-login-external-facebook");
    waitForSelectorVisible("*[name='login']");
    typeSelectorInputValue("#email", getFacebookUsername());
    typeSelectorInputValue("#pass", getFacebookPassword());
    clickSelector("*[name='login']");
    
    waitForSelectorPresent(".menu-tools-account");
    assertLoggedIn();
  }

  protected void loginGoogle() {
    acceptCookieDirective();
    navigate("/login/", true);
    waitForSelectorVisible(".user-login-external-google");
    clickSelector(".user-login-external-google");
    waitForSelectorVisible("#Email");
    waitAndClick("#Email");
    typeSelectorInputValue("#Email", getGoogleUsername());
    
    if (findElementsBySelector("#Passwd").isEmpty()) {
      clickSelector("#next");
    }
    
    waitForSelectorVisible("#Passwd");
    waitAndClick("#Passwd");
    typeSelectorInputValue("#Passwd", getGooglePassword());
    clickSelector("#signIn");
    waitForUrlMatches("^" + getAppUrl() + ".*");
    assertLoggedIn();
  }
  
  protected void logout() {
    navigate("/logout");
    assertSelectorNotPresent(".menu-tools-account");
    assertSelectorPresent(".menu-tools-login");
  }

  protected void assertLoggedIn() {
    assertSelectorNotVisible(".index-menu .menu-tools-login");
    assertSelectorVisible(".index-menu .menu-tools-account-container");
  }
  
  protected void assertNotLoggedIn() {
    assertSelectorVisible(".index-menu .menu-tools-login");
    assertSelectorNotVisible(".index-menu .menu-tools-account-container");
  }
  
  protected void waitSelectorToBeClickable(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> elements = findElementsBySelector(selector);
          if (elements.size() > 0) {
            return ExpectedConditions.elementToBeClickable(elements.get(0)).apply(driver) != null;
          }
        } catch (StaleElementReferenceException e) {
        }
        
        return false;
      }
    });
  }
  
  protected void waitForElementVisible(WebElement element) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOf(element));
  }

  protected void waitForSelectorVisible(String selector) {
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(selector)));
  }

  protected void waitForInputValueNotBlank(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> elements = driver.findElements(By.cssSelector(selector));
          if (elements.size() > 0) {
            return StringUtils.isNotBlank(elements.get(0).getAttribute("value"));
          }
        } catch (StaleElementReferenceException e) {
        }
        
        return false;
      }
    });
  }
  
  protected void waitForSelectorNotPresent(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> elements = findElementsBySelector(selector);
          if (elements.isEmpty()) {
            return true;
          }
        } catch (StaleElementReferenceException e) {
        }
        
        return false;
      }
    });
  }
  
  
  protected void waitForSelectorPresent(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return findElementsBySelector(selector).size() > 0;
      }
    });
  }
  
  protected void waitForSelectorCount(final String selector, final int count) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          return findElementsBySelector(selector).size() == count;
        } catch (StaleElementReferenceException e) {
          return false;
        }
      }
    });
  }
  
  protected WebElement findElementBySelector(String selector) {
    return getWebDriver().findElementByCssSelector(selector);
  }
  
  protected List<WebElement> findElementsBySelector(String selector) {
    try {
      return getWebDriver().findElementsByCssSelector(selector);
    } catch (NoSuchElementException e) {
      return Collections.emptyList();
    }
  }
  
  protected void assertSelectorText(String selector, String text) {
    assertSelectorText(selector, text, false, false);
  }
  
  protected void assertSelectorText(String selector, String text, boolean ignoreCase) {
    assertSelectorText(selector, text, ignoreCase, false);
  }
  
  protected void assertSelectorText(String selector, String text, boolean ignoreCase, boolean trim) {
    WebElement element = findElementBySelector(selector);
    
    String elementText = element.getText();
    if (trim) {
      elementText = StringUtils.trim(elementText);
    }
    
    if (ignoreCase) {
      assertTrue(String.format("Expected %s but was %s", text, elementText), text.equalsIgnoreCase(elementText));
    } else {
      assertTrue(String.format("Expected %s but was %s", text, elementText), text.equals(elementText));
    }
  }

  protected void assertSelectorTextIgnoreCase(String selector, String text) {
    assertSelectorText(selector, text, true);
  }
  
  protected void waitForUrlMatches(String regex) {
    waitForUrlMatches(getWebDriver(), regex);
  }

  protected void waitForUrlNotMatches(String regex) {
    waitForUrlNotMatches(getWebDriver(), regex);
  }
  
  protected void waitForSelectorText(final String selector, final String text) {
    waitForSelectorText(selector, text, false, false);
  }

  protected void waitForSelectorText(final String selector, final String text, boolean ignoreCase) {
    waitForSelectorText(selector, text, ignoreCase, false);
  }
  
  protected void waitForSelectorText(final String selector, final String text, final boolean ignoreCase, final boolean trim) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          WebElement element = findElementBySelector(selector);
          if (element != null) {
            String elementText = element.getText();
            if (trim) {
              elementText = StringUtils.trim(elementText);
            }
            
            if (ignoreCase) {
              return text.equalsIgnoreCase(elementText);
            } else {
              return text.equals(elementText);
            }
          }
        } catch (Exception e) {
        }
        
        return false;
      }
    }); 
  }
  
  protected void assertUrlMatches(String regex) {
    assertTrue("url '" + getWebDriver().getCurrentUrl() + "' does not match " + regex, getWebDriver().getCurrentUrl().matches(regex));
  }

  protected void waitTitle(String title) {
    new WebDriverWait(getWebDriver(), 60)
      .until(ExpectedConditions.titleIs(title));
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
  
  protected void testAccessDenied(String path) {
    testAccessDenied(path, false);
  }
  
  protected void testAccessDenied(String path, boolean secure) {
    navigate(path, secure);
    waitTitle("Access Denied!");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }

  protected void testTitle(String view, String expectedTitle) {
    testTitle(view, expectedTitle, false);
  }
  
  protected void testTitle(String view, String expectedTitle, boolean secure) {
    navigate(view, secure);
    waitTitle(expectedTitle);
    assertTitle(expectedTitle);
  }

  protected void testTitle(String expected) {
    assertTitle(expected);
  }
  
  protected void assertSelectorPresent(String selector) {
    assertTrue("Element not present '" + selector + "'", findElementsBySelector(selector).size() > 0);
  }
  
  protected void assertSelectorEnabled(String selector) {
    List<WebElement> elements = findElementsBySelector(selector);
    assertTrue("Element not present '" + selector + "'", elements.size() > 0);
    assertTrue("Element not enabled '" + selector + "'", elements.get(0).isEnabled());
  }
  
  protected void assertSelectorDisabled(String selector) {
    List<WebElement> elements = findElementsBySelector(selector);
    assertTrue("Element not present '" + selector + "'", elements.size() > 0);
    assertFalse("Element not disabled '" + selector + "'", elements.get(0).isEnabled());
  }

  protected void assertSelectorNotPresent(String selector) {
    assertTrue("Element present '" + selector + "'", findElementsBySelector(selector).size() == 0);
  }

  protected void assertSelectorVisible(String selector) {
    assertTrue("Element visible '" + selector + "'", getWebDriver().findElementByCssSelector(selector).isDisplayed());
  }

  protected void assertSelectorNotVisible(String selector) {
    if (findElementsBySelector(selector).size() == 0) {
      return;
    }
    
    assertTrue("Element not visible '" + selector + "'", !getWebDriver().findElementByCssSelector(selector).isDisplayed());
  }
  
  protected void assertSelectorClickable(String selector) {
    assertNotNull(ExpectedConditions.elementToBeClickable(findElementBySelector(selector)).apply(getWebDriver()));
  }
  
  protected void assertSelectorNotClickable(String selector) {
    assertNull(ExpectedConditions.elementToBeClickable(findElementBySelector(selector)).apply(getWebDriver()));
  }
  
  protected void assertSelectorCount(String selector, int expected) {
    assertEquals(expected, findElementsBySelector(selector).size());
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
  
  protected void waitAndClick(String selector) {
    waitSelectorToBeClickable(selector);
    clickSelector(selector);
  }

  protected void waitAndSendKeys(String selector, String keysToSend) {
    waitForSelectorVisible(selector);
    sendKeysSelector(selector, keysToSend);
  }

  protected void sendKeysSelector(String selector, String keysToSend) {
    getWebDriver().findElementByCssSelector(selector).sendKeys(keysToSend);
  }
  
  protected void testNotFound(String path) {
    navigate(path);
    waitTitle("Page Not Found!");
    assertEquals("Page Not Found!", getWebDriver().getTitle());
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
    executeScript(String.format("window.scrollBy(%d, %d)",  x, y));
  }
  
  protected void executeScript(String script) {
    ((JavascriptExecutor) getWebDriver()).executeScript(script, "");
  }
  
  protected void waitForPageLoad() {
    try {
      new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver driver) {
          return !((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
        }
      });
    } catch (Exception e) {
      
    }
    
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
        } catch (Exception e) {
          return false;
        }
      }
    });
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
