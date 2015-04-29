package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
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
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginGoogle();
    testNotFound(getWebDriver(), "/forge/import-google-drive?parentFolderId=12345");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testLoggedInWithGoogle() throws Exception {
    loginGoogle();
    testTitle(getWebDriver(), "/forge/import-google-drive", "Forge - Import From Google Drive");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testLoggedInWithFacebook() throws Exception {
    loginFacebook();
    getWebDriver().get(getAppUrl() + "/forge/import-google-drive");
    assertEquals("Sign in - Google Accounts", getWebDriver().getTitle());
    sleep(500);
    getWebDriver().findElement(By.name("Email")).sendKeys(getGoogleUsername());
    getWebDriver().findElement(By.name("Passwd")).sendKeys(getGooglePassword());
    getWebDriver().findElement(By.name("signIn")).click();
    testTitle(getWebDriver(), "/forge/import-google-drive", "Forge - Import From Google Drive");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testImportMaterial() throws Exception {
    loginGoogle();
    getWebDriver().get(getAppUrl() + "/forge/import-google-drive");
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-check-container input[type=\"checkbox\"]")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".forge-import-google-drive-button")));
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-button")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.titleIs("Forge"));
    assertEquals("Forge", getWebDriver().getTitle());
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-materials-list a[title=\"How to get started with Drive\"]")).size());
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testImportMaterialIntoFolder() throws Exception {
    loginGoogle();
    getWebDriver().get(getAppUrl() + "/forge/");
    getWebDriver().findElement(By.cssSelector(".forge-new-material-menu")).click();
    getWebDriver().findElement(By.cssSelector(".forge-new-material-menu .forge-new-material-folder")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".forge-create-folder-dialog")));
    getWebDriver().findElement(By.cssSelector(".forge-create-folder-dialog .forge-create-folder-name")).sendKeys("test folder");
    getWebDriver().findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();
    waitForUrlMatches(getWebDriver(), ".*/forge/folders/[0-9]{1,}/test_folder");
    getWebDriver().findElement(By.cssSelector(".forge-import-material-menu")).click();
    getWebDriver().findElement(By.cssSelector(".forge-import-material-menu .forge-import-google-drive")).click();
    assertUrlMatches(".*\\/import-google-drive\\?parentFolderId=[0-9]{1,}.*");
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-check-container input[type=\"checkbox\"]")).click();
    getWebDriver().findElement(By.cssSelector(".forge-import-google-drive-button")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.titleIs("Forge"));
    assertTrue(getWebDriver().getCurrentUrl(), getWebDriver().getCurrentUrl().matches(".*/forge/folders/[0-9]{1,}/test_folder"));
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-materials-list a[title=\"How to get started with Drive\"]")).size());
  }

}
