package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeVectorImageTestsBase extends AbstractUITest {

  private static final String VECTOR_IMAGE_IN_ROOT = "/forge/vectorimages/2/vectorimage";
  private static final String VECTOR_IMAGE_IN_FOLDER = "/forge/vectorimages/2/folder/vectorimage_in_folder";
  private static final String VECTOR_IMAGE_IN_SUBFOLDER = "/forge/vectorimages/2/folder/subfolder/vectorimage_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), VECTOR_IMAGE_IN_ROOT);
    testLoginRequired(getWebDriver(), VECTOR_IMAGE_IN_FOLDER);
    testLoginRequired(getWebDriver(), VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), VECTOR_IMAGE_IN_ROOT);
    testAccessDenied(getWebDriver(), VECTOR_IMAGE_IN_FOLDER);
    testAccessDenied(getWebDriver(), VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forge/vectorimages/2/folder/subfolder");
    testNotFound(getWebDriver(), "/forge/vectorimages//image");
    testNotFound(getWebDriver(), "/forge/vectorimages/a/image");
    testNotFound(getWebDriver(), "/forge/vectorimages/2");
    testNotFound(getWebDriver(), "/forge/vectorimages/2/");
    testNotFound(getWebDriver(), "/forge/vectorimages/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testMayViewVectorImage(VECTOR_IMAGE_IN_ROOT);
    testMayViewVectorImage(VECTOR_IMAGE_IN_FOLDER);
    testMayViewVectorImage(VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testMayEditVectorImage(VECTOR_IMAGE_IN_ROOT);
    testMayEditVectorImage(VECTOR_IMAGE_IN_FOLDER);
    testMayEditVectorImage(VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    loginInternal("user@foyt.fi", "pass");
    testMayEditVectorImage("/forge/vectorimages/2/vectorimage-hyphen");
  }
  
  @Test
  @SqlSets ({"basic-materials-users"})
  public void textCreateSharedFolder() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge/folders/2/folder");
    clickSelector(".forge-new-material-menu");
    clickSelector(".forge-new-new-vector-image");
    assertVectorImageEditable();
    executeSql("delete from VectorImage where id in (select id from Material where type='VECTOR_IMAGE' and parentFolder_id = 1)");
    executeSql("delete from Material where type='VECTOR_IMAGE' and parentFolder_id = 1");
  }

  private void testMayViewVectorImage(String path) {
    getWebDriver().get(getAppUrl() + path);
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container")).size());
    assertEquals(0, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container .forge-vector-image-save")).size());
  }

  private void testMayEditVectorImage(String path) {
    getWebDriver().get(getAppUrl() + path);
    assertVectorImageEditable();
  }

  private void assertVectorImageEditable() {
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container")).size());
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container .forge-vector-image-save")).size());
  }

}
