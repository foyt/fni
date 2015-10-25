package fi.foyt.fni.test.ui.base.forge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeGoogleDriveTestsBase extends AbstractUITest {

  private static final String GOOGLEDOC_IN_ROOT = "/forge/google-drive/2/googledoc";
  private static final String GOOGLEDOC_IN_FOLDER = "/forge/google-drive/2/folder/googledoc_in_folder";
  private static final String GOOGLEDOC_IN_SUBFOLDER = "/forge/google-drive/2/folder/subfolder/googledoc_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), GOOGLEDOC_IN_ROOT);
    testLoginRequired(getWebDriver(), GOOGLEDOC_IN_FOLDER);
    testLoginRequired(getWebDriver(), GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(GOOGLEDOC_IN_ROOT);
    testAccessDenied(GOOGLEDOC_IN_FOLDER);
    testAccessDenied(GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testMayView(getWebDriver(), GOOGLEDOC_IN_ROOT);
    testMayView(getWebDriver(), GOOGLEDOC_IN_FOLDER);
    testMayView(getWebDriver(), GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testMayView(getWebDriver(), GOOGLEDOC_IN_ROOT);
    testMayView(getWebDriver(), GOOGLEDOC_IN_FOLDER);
    testMayView(getWebDriver(), GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    loginInternal("user@foyt.fi", "pass");
    testMayView(getWebDriver(), "/forge/google-drive/2/googledoc-hyphen");
  }

  private void testMayView(RemoteWebDriver driver, String path) {
    getWebDriver().get(getAppUrl() + path);
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".forge-google-drive-container iframe")));
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-google-drive-container iframe")).size());
  }
}
