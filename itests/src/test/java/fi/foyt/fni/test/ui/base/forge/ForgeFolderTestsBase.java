package fi.foyt.fni.test.ui.base.forge;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeFolderTestsBase extends AbstractUITest {

  private static final String FOLDER = "/forge/folders/2/folder";
  private static final String SUBFOLDER = "/forge/folders/2/folder/subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(FOLDER);
    testLoginRequired(SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(FOLDER);
    testAccessDenied(SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/forge/folders/2/folder/image_in_folder");
    testNotFound("/forge/folders//folder");
    testNotFound("/forge/folders/a/folder");
    testNotFound("/forge/folders/2");
    testNotFound("/forge/folders/2/");
    testNotFound("/forge/folders/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    testMayViewFolder(FOLDER);
    testMayViewFolder(SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    testMayViewFolder(FOLDER);
    testMayViewFolder(SUBFOLDER);
  }

  private void testMayViewFolder(String path) {
    testTitle(path, "Forge");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testOpenShareDialog() {
    acceptCookieDirective();
    loginInternal("user@foyt.fi", "pass");
    navigate(FOLDER);
    waitAndClick(".forge-material[data-material-id=\"4\"] .forge-material-icon");
    waitAndClick(".forge-material[data-material-id=\"4\"] .forge-material-action-share a");
    waitForSelectorVisible(".forge-share-material-dialog");
    assertSelectorPresent(".forge-share-material-dialog");
  }

}
