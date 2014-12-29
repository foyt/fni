package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeIndexTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/forge/", "Forge");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/forge/");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testRemoveDialogLongText() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");

    new Actions(getWebDriver()).moveToElement(getWebDriver().findElement(By.cssSelector(".forge-material[data-material-id=\"15\"] .forge-material-info"))).build().perform();
    WebElement deleteLink = getWebDriver().findElement(By.cssSelector(".forge-material-action-delete a[data-material-id=\"15\"]"));
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.visibilityOf(deleteLink));
    deleteLink.click();

    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".forge-remove-material-dialog")));

    assertEquals("Remove 'Beowulf pohti zuluja ja ång...'?", getWebDriver().findElement(By.cssSelector(".ui-dialog-title")).getText());
    assertEquals("Do you really wish to remove 'Beowulf pohti zuluja ja ångström-yksikköä katsellessaan Q-stone- ja CMX-yhtyeitä videolta.'",
        getWebDriver().findElement(By.cssSelector(".forge-remove-material-dialog p")).getText());

  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testOpenShareDialog() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-material[data-material-id=\"16\"] .forge-material-icon");
    clickSelector(".forge-material[data-material-id=\"16\"] .forge-material-icon");
    waitSelectorToBeClickable(".forge-material[data-material-id=\"16\"] .forge-material-action-share a");
    clickSelector(".forge-material[data-material-id=\"16\"] .forge-material-action-share a");
    waitForSelectorVisible(".forge-share-material-dialog");
    assertSelectorPresent(".forge-share-material-dialog");
  }
  
}
