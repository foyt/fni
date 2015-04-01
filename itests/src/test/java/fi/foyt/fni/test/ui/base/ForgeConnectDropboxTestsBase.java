package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

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
  @DefineSqlSet (id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql")
})
public class ForgeConnectDropboxTestsBase extends AbstractUITest {
  
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
    testLoginRequired(getWebDriver(), "/forge/connect-dropbox");
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testConnect() {
    acceptCookieDirective(getWebDriver());

    loginGoogle();
    navigate("/forge/");
    waitAndClick(".forge-import-material-menu");
    waitAndClick(".forge-import-material-menu .forge-connect-dropbox");
    
    waitForUrlMatches(getWebDriver(), "^https://www.dropbox.com/1/oauth/authorize.*");
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOfElementLocated(By.id("login-content")));
    getWebDriver().findElement(By.cssSelector("#login-content input[type=\"email\"]")).click();
    getWebDriver().findElement(By.cssSelector("#login-content input[type=\"email\"]")).sendKeys(getDropboxUsername());
    getWebDriver().findElement(By.cssSelector("#login-content input[type=\"password\"]")).click();
    getWebDriver().findElement(By.cssSelector("#login-content input[type=\"password\"]")).sendKeys(getDropboxPassword());
    getWebDriver().findElement(By.cssSelector(".login-button")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.elementToBeClickable(By.name("allow_access")));
    getWebDriver().findElement(By.name("allow_access")).click();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.titleIs("Forge"));
    assertEquals("Forge", getWebDriver().getTitle());
    waitForNotification(getWebDriver());
    assertNotificationStartsWith(getWebDriver(), "info", "Dropbox folder is connected");
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-materials-list a[title=\"Dropbox\"]")).size());
  }
  
}
