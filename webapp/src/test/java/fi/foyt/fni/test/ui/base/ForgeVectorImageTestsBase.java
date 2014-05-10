package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

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
  public void testForbidden() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), VECTOR_IMAGE_IN_ROOT);
    testAccessDenied(getWebDriver(), VECTOR_IMAGE_IN_FOLDER);
    testAccessDenied(getWebDriver(), VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
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
  public void testMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testMayViewVectorImage(getWebDriver(), VECTOR_IMAGE_IN_ROOT);
    testMayViewVectorImage(getWebDriver(), VECTOR_IMAGE_IN_FOLDER);
    testMayViewVectorImage(getWebDriver(), VECTOR_IMAGE_IN_SUBFOLDER);
  }

  @Test
  public void testMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testMayEditVectorImage(getWebDriver(), VECTOR_IMAGE_IN_ROOT);
    testMayEditVectorImage(getWebDriver(), VECTOR_IMAGE_IN_FOLDER);
    testMayEditVectorImage(getWebDriver(), VECTOR_IMAGE_IN_SUBFOLDER);
  }

  private void testMayViewVectorImage(RemoteWebDriver driver, String path) {
    getWebDriver().get(getAppUrl() + path);
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container")).size());
    assertEquals(0, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container .forge-vector-image-save")).size());
  }

  private void testMayEditVectorImage(RemoteWebDriver driver, String path) {
    getWebDriver().get(getAppUrl() + path);
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container")).size());
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-vector-image-container .forge-vector-image-save")).size());
  }

}
