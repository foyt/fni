package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeFolderTestsBase extends AbstractUITest {

  private static final String FOLDER = "/forge/folders/2/folder";
  private static final String SUBFOLDER = "/forge/folders/2/folder/subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), FOLDER);
    testLoginRequired(getWebDriver(), SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), FOLDER);
    testAccessDenied(getWebDriver(), SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forge/folders/2/folder/image_in_folder");
    testNotFound(getWebDriver(), "/forge/folders//folder");
    testNotFound(getWebDriver(), "/forge/folders/a/folder");
    testNotFound(getWebDriver(), "/forge/folders/2");
    testNotFound(getWebDriver(), "/forge/folders/2/");
    testNotFound(getWebDriver(), "/forge/folders/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testMayViewFolder(getWebDriver(), FOLDER);
    testMayViewFolder(getWebDriver(), SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testMayViewFolder(getWebDriver(), FOLDER);
    testMayViewFolder(getWebDriver(), SUBFOLDER);
  }

  private void testMayViewFolder(RemoteWebDriver driver, String path) {
    driver.get(getAppUrl() + path);
    assertEquals("Forge", driver.getTitle());
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testOpenShareDialog() {
    acceptCookieDirective();
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    navigate(FOLDER);
    waitForSelectorVisible(".forge-material[data-material-id=\"4\"] .forge-material-icon");
    clickSelector(".forge-material[data-material-id=\"4\"] .forge-material-icon");
    waitSelectorToBeClickable(".forge-material[data-material-id=\"4\"] .forge-material-action-share a");
    clickSelector(".forge-material[data-material-id=\"4\"] .forge-material-action-share a");
    waitForSelectorVisible(".forge-share-material-dialog");
    assertSelectorPresent(".forge-share-material-dialog");
  }

}
