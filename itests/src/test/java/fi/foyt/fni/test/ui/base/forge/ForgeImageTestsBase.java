package fi.foyt.fni.test.ui.base.forge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeImageTestsBase extends AbstractUITest {

  private static final String IMAGE_IN_ROOT = "/forge/images/2/image";
  private static final String IMAGE_IN_FOLDER = "/forge/images/2/folder/image_in_folder";
  private static final String IMAGE_IN_SUBFOLDER = "/forge/images/2/folder/subfolder/image_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(IMAGE_IN_ROOT);
    testLoginRequired(IMAGE_IN_FOLDER);
    testLoginRequired(IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(IMAGE_IN_ROOT);
    testAccessDenied(IMAGE_IN_FOLDER);
    testAccessDenied(IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/forge/images/2/folder/subfolder");
    testNotFound("/forge/images//image");
    testNotFound("/forge/images/a/image");
    testNotFound("/forge/images/2");
    testNotFound("/forge/images/2/");
    testNotFound("/forge/images/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    testMayViewImage(IMAGE_IN_ROOT);
    testMayViewImage(IMAGE_IN_FOLDER);
    testMayViewImage(IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    testMayViewImage(IMAGE_IN_ROOT);
    testMayViewImage(IMAGE_IN_FOLDER);
    testMayViewImage(IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    loginInternal("user@foyt.fi", "pass");
    testMayViewImage("/forge/images/2/image-hyphen");
  }

  private void testMayViewImage(String path) {
    getWebDriver().get(getAppUrl() + path);
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-image-container img")).size());
  }

}
