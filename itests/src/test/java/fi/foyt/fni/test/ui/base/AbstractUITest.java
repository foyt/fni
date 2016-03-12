package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.saucelabs.common.SauceOnDemandSessionIdProvider;

public class AbstractUITest extends fi.foyt.fni.test.ui.AbstractUITest implements SauceOnDemandSessionIdProvider {

  @Rule
  public TestWatcher testWatcher = new TestWatcher() {
    
    @Override
    protected void failed(Throwable e, Description description) {
      try {
        takeScreenshot();
      } catch (WebDriverException | IOException e1) {
        e1.printStackTrace();
      }
    }
    
    protected void finished(Description description) {
      try {
        getWebDriver().quit();
      } catch (Exception e) {
      }
    };
    
  };
  
  @Override
  public String getSessionId() {
    return sessionId;
  }
  
  protected void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
    
    if (webDriver instanceof RemoteWebDriver) {
      this.sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
    }
  }
  
  protected WebDriver getWebDriver() {
    return webDriver;
  }
  
  protected RemoteWebDriver createSauceWebDriver() throws MalformedURLException {
    String browser = getBrowser();
    String version = getBrowserVersion();
    String platform = getPlatform();
    
    if (StringUtils.isBlank(version)) {
      version = "latest";
    }
    
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
    capabilities.setCapability("commandTimeout", 600);

    if (getSauceTunnelId() != null) {
      capabilities.setCapability("tunnel-identifier", getSauceTunnelId());
    }
    
    RemoteWebDriver driver = new RemoteWebDriver(new URL(String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", getSauceUsername(), getSauceAccessKey())), capabilities);
    driver.setFileDetector(new LocalFileDetector());
    
    return driver;
  }

  protected WebDriver createLocalDriver() {
    switch (getBrowser()) {
      case "chrome":
        return createChromeDriver();
      case "phantomjs":
        return createPhantomJsDriver();
    }
    
    throw new RuntimeException(String.format("Unknown browser %s", getBrowser()));
  }

  protected WebDriver createChromeDriver() {
    ChromeDriver driver = new ChromeDriver();
    return driver;
  }
  
  protected WebDriver createPhantomJsDriver() {
    DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, ".phantomjs/bin/phantomjs");
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--ignore-ssl-errors=true", "--webdriver-loglevel=NONE", "--load-images=false" } );
    PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
    driver.manage().window().setSize(new Dimension(1024, 768));
    return driver;
  }
  
  protected void loginInternal(String email, String password) {
    String loginUrl = getAppUrl(true) + "/login/";
    if (!StringUtils.startsWith(getWebDriver().getCurrentUrl(), loginUrl)) {
      navigateAndWait("/login/", true);
    }
    
    scrollWaitAndType(".user-login-email", email);
    scrollWaitAndType(".user-login-password", password);
    scrollWaitAndClick(".user-login-button");
    
    waitForSelectorVisible(".menu-tools-account");

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
    navigate("/login/", true);
    waitAndClick(".user-login-external-google");
    waitAndClick("#Email");
    typeSelectorInputValue("#Email", getGoogleUsername());
    
    if (findElementsBySelector("#Passwd").isEmpty()) {
      clickSelector("#next");
    }
    
    waitAndClick("#Passwd");
    typeSelectorInputValue("#Passwd", getGooglePassword());
    waitAndClick("#signIn");
    waitForSelectorVisible(".menu-tools-account");
    assertLoggedIn();
  }
  
  protected void logout() {
    navigate("/logout");
    waitForSelectorPresent(".menu-tools-login");
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
  
  protected void waitForSelectorVisible(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        List<WebElement> elements = findElementsBySelector(selector);
        if (elements.isEmpty()) {
          return false;
        }
        
        for (WebElement element : elements) {
          if (!element.isDisplayed()){
            return false;
          }
        }
        
        return true;
      }
    });
  }

  protected void waitNotVisible(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        List<WebElement> elements = findElementsBySelector(selector);
        if (elements.isEmpty()) {
          return true;
        }
        
        for (WebElement element : elements) {
          if (element.isDisplayed()){
            return false;
          }
        }
        
        return true;
      }
    });
  }
  
  protected void waitForInputValueNotBlank(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> elements = driver.findElements(By.cssSelector(selector));
          if (elements.size() > 0) {
            return StringUtils.isNotBlank(elements.get(0).getAttribute("value"));
          }
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
          return false;
        }
      }
    });
  }
  
  protected WebElement findElementBySelector(String selector) {
    return ((FindsByCssSelector) getWebDriver()).findElementByCssSelector(selector);
  }
  
  protected List<WebElement> findElementsBySelector(String selector) {
    try {
      return ((FindsByCssSelector) getWebDriver()).findElementsByCssSelector(selector);
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
      assertTrue(String.format("Expected '%s' but was '%s'", text, elementText), text.equalsIgnoreCase(elementText));
    } else {
      assertTrue(String.format("Expected '%s' but was '%s'", text, elementText), text.equals(elementText));
    }
  }

  protected void assertSelectorTextIgnoreCase(String selector, String text) {
    assertSelectorText(selector, text, true);
  }
  

  protected void waitAndAssertSelectorText(String selector, String text, boolean ignoreCase, boolean trim) {
    waitForSelectorPresent(selector);
    assertSelectorText(selector, text, ignoreCase, trim);
  }
  
  protected void waitForUrlMatches(final String regex) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.getCurrentUrl().matches(regex);
      }
    });
  }

  protected void waitForUrlNotMatches(final String regex) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return !driver.getCurrentUrl().matches(regex);
      }
    });
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

  protected void waitTitle(final String title) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        try {
          return title.equals(driver.getTitle());
        } catch (Exception e) {
        }
        
        return false;
      }
    });
  }

  protected void waitForNotification() {
    waitForSelectorPresent(".notifications .notification");
  }

  protected void assertNotification(String serverity, String text) {
    assertSelectorText(".notification-" + serverity, text, true, true);
  }
  
  protected void assertNotificationStartsWith(String serverity, String text) {
    assertTrue(StringUtils.startsWithIgnoreCase(findElementBySelector(".notification-" + serverity).getText(), text));
  }
  
  protected void navigate(String path) {
    navigate(path, false);
  }
  
  protected void navigate(String path, Boolean secure) {
    String url = String.format("%s%s", getAppUrl(secure), path);
    int i = 0;
    
    while (!tryNavigate(url)) {
      i++;
      if (i > 4) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, String.format("Failed to navigate to url %s", url));
        return;
      }
    }
  }
  
  private boolean tryNavigate(String url) {
    getWebDriver().get(url);
    String currentUrl = getWebDriver().getCurrentUrl();
    
    if (StringUtils.isBlank(currentUrl) || StringUtils.startsWith(currentUrl, "about:")) {
      return false; 
    }
    
    return true;
  }

  protected void navigateAndWait(String path) {
    navigateAndWait(path, false);
  }

  protected void navigateAndWait(String path, Boolean secure) {
    navigate(path, secure);
    String url = String.format("%s%s", getAppUrl(secure), path);
    waitForUrl(url);
  }

  protected void waitForUrl(final String url) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return url.equals(driver.getCurrentUrl());
      }
    });
  }
  
  protected void testLoginRequired(String path) throws UnsupportedEncodingException {
    testLoginRequired(path, false);  
  }
  
  protected void testLoginRequired(String path, boolean secure) throws UnsupportedEncodingException {
    navigate(path, secure);
    String ctxPath = getCtxPath();
    String expectedUrl = getAppUrl(true) + "/login/?redirectUrl=" + URLEncoder.encode(ctxPath != null ? "/" + ctxPath + path : path, "UTF-8");
    waitForUrlMatches("https://.*");
    assertEquals(expectedUrl, getWebDriver().getCurrentUrl());
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
    testTitle(expectedTitle);
  }

  protected void testTitle(String expected) {
    waitTitle(expected);
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
    WebElement element = findElementBySelector(selector);
    assertNotNull("Element not present '" + selector + "'", element);
    assertTrue("Element visible '" + selector + "'", element.isDisplayed());
  }

  protected void assertSelectorNotVisible(String selector) {
    if (findElementsBySelector(selector).size() == 0) {
      return;
    }
    
    assertTrue("Element not visible '" + selector + "'", !findElementBySelector(selector).isDisplayed());
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

  protected void assertSelectorValueNot(String selector, String expected) {
    assertNotEquals(expected, findElementBySelector(selector).getAttribute("value"));
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
    findElementBySelector(selector).click();
  }
  
  protected void scrollWaitAndClick(String selector) {
    scrollIntoView(selector);
    waitSelectorToBeClickable(selector);
    clickSelector(selector);
  }
  
  protected void waitAndClick(String selector) {
    waitSelectorToBeClickable(selector);
    clickSelector(selector);
  }

  protected void waitAndSendKeys(String selector, String keysToSend) {
    waitForSelectorVisible(selector);
    sendKeysSelector(selector, keysToSend);
  }
  
  protected void scrollWaitAndType(String selector, String keysToSend) {
    waitForSelectorPresent(selector);
    scrollIntoView(selector);
    waitForSelectorVisible(selector);
    sendKeysSelector(selector, keysToSend);
  }

  protected void sendKeysSelector(String selector, String keysToSend) {
    findElementBySelector(selector).sendKeys(keysToSend);
  }
  
  protected void testNotFound(String path) {
    navigate(path);
    waitTitle("Page Not Found!");
    assertEquals("Page Not Found!", getWebDriver().getTitle());
  }
  
  protected void testNotFound(String path, boolean secure) {
    navigate(path, secure);
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
//    waitAndClick(".cc_banner-wrapper .cc_btn_accept_all");
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
  
  protected void scrollIntoView(String selector) {
    ((JavascriptExecutor) getWebDriver()).executeScript(String.format("document.querySelectorAll('%s').item(0).scrollIntoView(true);", selector));
  }
  
  protected void executeScript(String script) {
    ((JavascriptExecutor) getWebDriver()).executeScript(script, "");
  }
  
  protected void waitForPageLoad() {
    try {
      new WebDriverWait(getWebDriver(), 5).until(new ExpectedCondition<Boolean>() {
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
     fileOuputStream.write(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES));
    } finally {
      fileOuputStream.flush();
      fileOuputStream.close();
    }
  }

  protected void acceptPaytrailPayment() {
    waitAndClick("input[value=\"Osuuspankki\"]");
    waitForUrl("https://kultaraha.op.fi/cgi-bin/krcgi");

    waitAndSendKeys("*[name='id']", "123456");
    waitAndSendKeys("*[name='pw']", "7890");
    waitAndClick("*[name='ktunn']");

    waitAndSendKeys("*[name='avainluku']", "1234");
    waitAndClick("*[name='avainl']");
    waitAndClick("#Toiminto");
  }
  
  private String sessionId;
  private WebDriver webDriver;
}
