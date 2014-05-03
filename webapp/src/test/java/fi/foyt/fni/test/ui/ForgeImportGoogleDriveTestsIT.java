package fi.foyt.fni.test.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;

public class ForgeImportGoogleDriveTestsIT extends AbstractUITest {
  
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
      testLoginRequired(driver, "/forge/import-google-drive");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLoggedInWithGoogle() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginGoogle(driver);
      testTitle(driver, "/forge/import-google-drive", "Forge - Import From Google Drive");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLoggedInWithFacebook() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginFacebook(driver);
      driver.get(getAppUrl() + "/forge/import-google-drive");
      assertEquals("Sign in - Google Accounts", driver.getTitle());
      driver.findElement(By.name("Email")).sendKeys(getGoogleUsername());
      driver.findElement(By.name("Passwd")).sendKeys(getGooglePassword());
      driver.findElement(By.name("signIn")).click();
      testTitle(driver, "/forge/import-google-drive", "Forge - Import From Google Drive");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testImportMaterial() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginGoogle(driver);
      driver.get(getAppUrl() + "/forge/import-google-drive");
      driver.findElement(By.cssSelector(".forge-import-google-drive-check-container input[type=\"checkbox\"]")).click();
      new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".forge-import-google-drive-button")));
      driver.findElement(By.cssSelector(".forge-import-google-drive-button")).click();
      new WebDriverWait(driver, 60).until(ExpectedConditions.titleIs("Forge"));
      assertEquals("Forge", driver.getTitle());
      assertEquals(1, driver.findElements(By.cssSelector(".forge-materials-list a[title=\"How to get started with Drive\"]")).size());
    } finally {
      driver.close();
    }
  }
  
}
