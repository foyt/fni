package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginGoogle(driver);
      testNotFound(driver, "/forge/import-google-drive?parentFolderId=12345");
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
  
  @Test
  public void testImportMaterialIntoFolder() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginGoogle(driver);
      driver.get(getAppUrl() + "/forge/");
      driver.findElement(By.cssSelector(".forge-new-material-menu")).click();
      driver.findElement(By.cssSelector(".forge-new-material-menu .forge-new-material-folder")).click();
      new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".forge-create-folder-dialog")));
      driver.findElement(By.cssSelector(".forge-create-folder-dialog .forge-create-folder-name")).sendKeys("test folder");
      driver.findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();
      waitForUrlMatches(driver, ".*/forge/folders/[0-9]{1,}/test_folder");
      driver.findElement(By.cssSelector(".forge-import-material-menu")).click();
      driver.findElement(By.cssSelector(".forge-import-material-menu .forge-import-google-drive")).click();
      assertTrue(driver.getCurrentUrl(), driver.getCurrentUrl().matches(".*\\/import-google-drive\\?parentFolderId=[0-9]{1,}\\b"));
      driver.findElement(By.cssSelector(".forge-import-google-drive-check-container input[type=\"checkbox\"]")).click();
      driver.findElement(By.cssSelector(".forge-import-google-drive-button")).click();
      new WebDriverWait(driver, 60).until(ExpectedConditions.titleIs("Forge"));
      assertTrue(driver.getCurrentUrl(), driver.getCurrentUrl().matches(".*/forge/folders/[0-9]{1,}/test_folder"));
      assertEquals(1, driver.findElements(By.cssSelector(".forge-materials-list a[title=\"How to get started with Drive\"]")).size());
    } finally {
      driver.close();
    }
  }
  
}
