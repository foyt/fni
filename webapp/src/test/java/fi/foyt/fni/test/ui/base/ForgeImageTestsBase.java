package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeImageTestsBase extends AbstractUITest {

  private static final String IMAGE_IN_ROOT = "/forge/images/2/image";
  private static final String IMAGE_IN_FOLDER = "/forge/images/2/folder/image_in_folder";
  private static final String IMAGE_IN_SUBFOLDER = "/forge/images/2/folder/subfolder/image_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), IMAGE_IN_ROOT);
    testLoginRequired(getWebDriver(), IMAGE_IN_FOLDER);
    testLoginRequired(getWebDriver(), IMAGE_IN_SUBFOLDER);
  }

  @Test
  public void testForbidden() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), IMAGE_IN_ROOT);
    testAccessDenied(getWebDriver(), IMAGE_IN_FOLDER);
    testAccessDenied(getWebDriver(), IMAGE_IN_SUBFOLDER);
  }

  @Test
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forge/images/2/folder/subfolder");
    testNotFound(getWebDriver(), "/forge/images//image");
    testNotFound(getWebDriver(), "/forge/images/a/image");
    testNotFound(getWebDriver(), "/forge/images/2");
    testNotFound(getWebDriver(), "/forge/images/2/");
    testNotFound(getWebDriver(), "/forge/images/2/*");
  }

  @Test
  public void testMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testMayViewImage(getWebDriver(), IMAGE_IN_ROOT);
    testMayViewImage(getWebDriver(), IMAGE_IN_FOLDER);
    testMayViewImage(getWebDriver(), IMAGE_IN_SUBFOLDER);
  }

  @Test
  public void testMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testMayViewImage(getWebDriver(), IMAGE_IN_ROOT);
    testMayViewImage(getWebDriver(), IMAGE_IN_FOLDER);
    testMayViewImage(getWebDriver(), IMAGE_IN_SUBFOLDER);
  }

  private void testMayViewImage(RemoteWebDriver driver, String path) {
    getWebDriver().get(getAppUrl() + path);
    assertEquals(1, getWebDriver().findElements(By.cssSelector(".forge-image-container img")).size());
  }

}
