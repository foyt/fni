package fi.foyt.fni.test.ui.base.forge;

import org.junit.Test;

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
    testLoginRequired(GOOGLEDOC_IN_ROOT);
    testLoginRequired(GOOGLEDOC_IN_FOLDER);
    testLoginRequired(GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(GOOGLEDOC_IN_ROOT);
    testAccessDenied(GOOGLEDOC_IN_FOLDER);
    testAccessDenied(GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    testMayView(GOOGLEDOC_IN_ROOT);
    testMayView(GOOGLEDOC_IN_FOLDER);
    testMayView(GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    testMayView(GOOGLEDOC_IN_ROOT);
    testMayView(GOOGLEDOC_IN_FOLDER);
    testMayView(GOOGLEDOC_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    loginInternal("user@foyt.fi", "pass");
    testMayView("/forge/google-drive/2/googledoc-hyphen");
  }

  private void testMayView(String path) {
    navigate(path);
    waitForSelectorPresent(".forge-google-drive-container iframe");
    assertSelectorPresent(".forge-google-drive-container iframe");
  }
}
