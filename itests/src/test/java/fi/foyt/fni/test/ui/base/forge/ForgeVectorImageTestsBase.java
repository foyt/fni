package fi.foyt.fni.test.ui.base.forge;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeVectorImageTestsBase extends AbstractUITest {

  private static final String VECTOR_IMAGE_IN_ROOT = "/forge/vectorimages/2/vectorimage";
  private static final String VECTOR_IMAGE_IN_FOLDER = "/forge/vectorimages/2/folder/vectorimage_in_folder";
  private static final String VECTOR_IMAGE_IN_SUBFOLDER = "/forge/vectorimages/2/folder/subfolder/vectorimage_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(VECTOR_IMAGE_IN_ROOT);
    testLoginRequired(VECTOR_IMAGE_IN_FOLDER);
    testLoginRequired(VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(VECTOR_IMAGE_IN_ROOT);
    testAccessDenied(VECTOR_IMAGE_IN_FOLDER);
    testAccessDenied(VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/forge/vectorimages/2/folder/subfolder");
    testNotFound("/forge/vectorimages//image");
    testNotFound("/forge/vectorimages/a/image");
    testNotFound("/forge/vectorimages/2");
    testNotFound("/forge/vectorimages/2/");
    testNotFound("/forge/vectorimages/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    if ("phantomjs".equals(getBrowser())) {
      // PhantomJs crashes on this test.
      return;
    }
    
    loginInternal("librarian@foyt.fi", "pass");
    testMayViewVectorImage(VECTOR_IMAGE_IN_ROOT);
    testMayViewVectorImage(VECTOR_IMAGE_IN_FOLDER);
    testMayViewVectorImage(VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    if ("phantomjs".equals(getBrowser())) {
      // PhantomJs crashes on this test.
      return;
    }
    
    loginInternal("admin@foyt.fi", "pass");
    testMayEditVectorImage(VECTOR_IMAGE_IN_ROOT);
    testMayEditVectorImage(VECTOR_IMAGE_IN_FOLDER);
    testMayEditVectorImage(VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    if ("phantomjs".equals(getBrowser())) {
      // PhantomJs crashes on this test.
      return;
    }
    
    loginInternal("user@foyt.fi", "pass");
    testMayEditVectorImage("/forge/vectorimages/2/vectorimage-hyphen");
  }
  
  @Test
  @SqlSets ({"basic-materials-users"})
  public void testCreateSharedFolder() throws Exception {
    if ("phantomjs".equals(getBrowser())) {
      // PhantomJs crashes on this test.
      return;
    }
    
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge/folders/2/folder");
    waitAndClick(".forge-new-material-menu");
    waitAndClick(".forge-new-new-vector-image");
    assertVectorImageEditable();
    executeSql("delete from VectorImage where id in (select id from Material where type='VECTOR_IMAGE' and parentFolder_id = 1)");
    executeSql("delete from MaterialView where material_id in (select id from Material where type='VECTOR_IMAGE' and parentFolder_id = 1)");
    executeSql("delete from Material where type='VECTOR_IMAGE' and parentFolder_id = 1");
  }

  private void testMayViewVectorImage(String path) {
    getWebDriver().get(getAppUrl() + path);
    assertSelectorPresent(".forge-vector-image-container");
    assertSelectorNotPresent(".forge-vector-image-container .forge-vector-image-save");
  }

  private void testMayEditVectorImage(String path) {
    getWebDriver().get(getAppUrl() + path);
    assertVectorImageEditable();
  }

  private void assertVectorImageEditable() {
    waitForSelectorVisible(".forge-vector-image-container");
    assertSelectorVisible(".forge-vector-image-container");
    waitForSelectorVisible(".forge-vector-image-container .forge-vector-image-save");
    assertSelectorVisible(".forge-vector-image-container .forge-vector-image-save");
  }

}
