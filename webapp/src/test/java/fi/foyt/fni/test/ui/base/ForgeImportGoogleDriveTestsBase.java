package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ForgeImportGoogleDriveTestsBase extends AbstractUITest {

  @Before
  public void baseSetUp() throws Exception {
    createOAuthSettings();
  }

  @After
  public void baseTearDown() throws Exception {
    purgeOAuthSettings();
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/forge/import-google-drive");
  }

  @Test
  public void testNotFound() throws Exception {
    loginGoogle(getWebDriver());
    testNotFound(getWebDriver(), "/forge/import-google-drive?parentFolderId=12345");
  }

  @Test
  public void testLoggedInWithGoogle() throws Exception {
    loginGoogle(getWebDriver());
    testTitle(getWebDriver(), "/forge/import-google-drive", "Forge - Import From Google Drive");
  }

  @Test
  public void testLoggedInWithFacebook() throws Exception {
    loginFacebook(getWebDriver());
    getWebDriver().get(getAppUrl() + "/forge/import-google-drive");
    assertEquals("Sign in - Google Accounts", getWebDriver().getTitle());
    getWebDriver().findElement(By.name("Email")).sendKeys(getGoogleUsername());
    getWebDriver().findElement(By.name("Passwd")).sendKeys(getGooglePassword());
    getWebDriver().findElement(By.name("signIn")).click();
    testTitle(getWebDriver(), "/forge/import-google-drive", "Forge - Import From Google Drive");
  }

  @Test
  public void testImportMaterial() throws Exception {
    loginGoogle(getWebDriver());
    getWebDriver().get(getAppUrl() + "/forge/import-google-drive");
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-check-container input[type=\"checkbox\"]")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".forge-import-google-drive-button")));
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-button")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.titleIs("Forge"));
    assertEquals("Forge", getWebDriver().getTitle());
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-materials-list a[title=\"How to get started with Drive\"]")).size());
  }

  @Test
  public void testImportMaterialIntoFolder() throws Exception {
    loginGoogle(getWebDriver());
    getWebDriver().get(getAppUrl() + "/forge/");
    getWebDriver().findElement(By.cssSelector(".forge-new-material-menu")).click();
    getWebDriver().findElement(By.cssSelector(".forge-new-material-menu .forge-new-material-folder")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".forge-create-folder-dialog")));
    getWebDriver().findElement(By.cssSelector(".forge-create-folder-dialog .forge-create-folder-name")).sendKeys("test folder");
    getWebDriver().findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();
    waitForUrlMatches(getWebDriver(), ".*/forge/folders/[0-9]{1,}/test_folder");
    getWebDriver().findElement(By.cssSelector(".forge-import-material-menu")).click();
    getWebDriver().findElement(By.cssSelector(".forge-import-material-menu .forge-import-google-drive")).click();
    assertTrue(getWebDriver().getCurrentUrl(), getWebDriver().getCurrentUrl().matches(".*\\/import-google-drive\\?parentFolderId=[0-9]{1,}\\b"));
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-check-container input[type=\"checkbox\"]")).click();
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-button")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.titleIs("Forge"));
    assertTrue(getWebDriver().getCurrentUrl(), getWebDriver().getCurrentUrl().matches(".*/forge/folders/[0-9]{1,}/test_folder"));
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-materials-list a[title=\"How to get started with Drive\"]")).size());
  }

}
