package fi.foyt.fni.test.ui.base.forge;

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
import fi.foyt.fni.test.ui.base.AbstractUITest;

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
    testLoginRequired("/forge/import-google-drive");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginGoogle();
    testNotFound("/forge/import-google-drive?parentFolderId=12345");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testLoggedInWithGoogle() throws Exception {
    loginGoogle();
    navigate("/forge/import-google-drive");
    
    waitPresent("#submit_approve_access", ".forge-import-google-drive-check-container");
    if (!findElements("#submit_approve_access").isEmpty()) {
      waitAndClick("#submit_approve_access");
    }

    waitPresent(".forge-import-google-drive-check-container");

    assertTitle("Forge - Import From Google Drive");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testLoggedInWithFacebook() throws Exception {
    loginFacebook();
    navigate("/forge/import-google-drive");
    
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
    
    waitPresent("#submit_approve_access", ".menu-tools-account");
    if (!findElements("#submit_approve_access").isEmpty()) {
      waitAndClick("#submit_approve_access");
    }
    
    waitTitle("Forge - Import From Google Drive");
    assertTitle("Forge - Import From Google Drive");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testImportMaterial() throws Exception {
    loginGoogle();
    getWebDriver().get(getAppUrl() + "/forge/import-google-drive");
    
    waitPresent("#submit_approve_access", ".forge-import-google-drive-check-container");
    if (!findElements("#submit_approve_access").isEmpty()) {
      waitAndClick("#submit_approve_access");
    }
    
    scrollWaitAndClick(".forge-import-google-drive-check-container input[type=\"checkbox\"]");
    scrollWaitAndClick(".forge-import-google-drive-button");
    
    waitTitle("Forge");
    assertEquals("Forge", getWebDriver().getTitle());
    assertEquals(2, getWebDriver().findElements(By.cssSelector(".forge-material-title[title=\"How to get started with Drive\"]")).size());
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
    waitForUrlMatches(".*/forge/folders/[0-9]{1,}/test_folder");
    waitAndClick(".forge-import-material-menu");
    waitAndClick(".forge-import-material-menu .forge-import-google-drive");
    
    waitPresent("#submit_approve_access", ".forge-import-google-drive-check-container");
    if (!findElements("#submit_approve_access").isEmpty()) {
      waitAndClick("#submit_approve_access");
    }
    
    waitForUrlMatches(".*\\/import-google-drive\\?parentFolderId=[0-9]{1,}.*");
    assertUrlMatches(".*\\/import-google-drive\\?parentFolderId=[0-9]{1,}.*");
    waitAndClick(".forge-import-google-drive-check-container input[type=\"checkbox\"]");
    waitAndClick(".forge-import-google-drive-button");
    waitTitle("Forge");
    assertTrue(getWebDriver().getCurrentUrl(), getWebDriver().getCurrentUrl().matches(".*/forge/folders/[0-9]{1,}/test_folder"));
    assertEquals(2, getWebDriver().findElements(By.cssSelector(".forge-material-title[title=\"How to get started with Drive\"]")).size());
  }

}
