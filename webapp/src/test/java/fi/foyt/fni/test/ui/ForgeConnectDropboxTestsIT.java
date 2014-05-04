package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ForgeConnectDropboxTestsIT extends AbstractUITest {
  
  @Before
  public void setup() throws Exception {
    createOAuthSettings();
  }
  
  @After
  public void tearDown() throws Exception {
    purgeOAuthSettings();
  }
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/forge/connect-dropbox");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testConnect() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginGoogle(driver);
      driver.get(getAppUrl() + "/forge/");
      driver.findElement(By.cssSelector(".forge-import-material-menu")).click();
      driver.findElement(By.cssSelector(".forge-import-material-menu .forge-connect-dropbox")).click();

      waitForUrlMatches(driver, "^https://www.dropbox.com/1/oauth/authorize.*");
      new WebDriverWait(driver, 60).until(ExpectedConditions.visibilityOfElementLocated(By.id("login-content")));
      driver.findElement(By.cssSelector("#login-content input[type=\"email\"]")).click();
      driver.findElement(By.cssSelector("#login-content input[type=\"email\"]")).sendKeys(getDropboxUsername());
      driver.findElement(By.cssSelector("#login-content input[type=\"password\"]")).click();
      driver.findElement(By.cssSelector("#login-content input[type=\"password\"]")).sendKeys(getDropboxPassword());
      driver.findElement(By.cssSelector(".login-button")).click();
      new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.name("allow_access")));
      driver.findElement(By.name("allow_access")).click();
      new WebDriverWait(driver, 60).until(ExpectedConditions.titleIs("Forge"));
      assertEquals("Forge", driver.getTitle());
      new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".jsf-messages-container li.info span")));
      assertTrue(StringUtils.startsWithIgnoreCase(driver.findElement(By.cssSelector(".jsf-messages-container li.info span")).getText(), "Dropbox folder is connected"));
      assertEquals(1, driver.findElements(By.cssSelector(".forge-materials-list a[title=\"Dropbox\"]")).size());
    } finally {
      driver.close();
    }
  }
  
}
