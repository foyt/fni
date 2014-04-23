package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeImageTestsIT extends AbstractUITest {
 
  private static final String IMAGE_IN_ROOT = "/forge/images/2/image";
  private static final String IMAGE_IN_FOLDER = "/forge/images/2/folder/image_in_folder";
  private static final String IMAGE_IN_SUBFOLDER = "/forge/images/2/folder/subfolder/image_in_subfolder";
    
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, IMAGE_IN_ROOT);
      testLoginRequired(driver, IMAGE_IN_FOLDER);
      testLoginRequired(driver, IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testForbidden() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, IMAGE_IN_ROOT);
      testAccessDenied(driver, IMAGE_IN_FOLDER);
      testAccessDenied(driver, IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testNotFound(driver, "/forge/images/2/folder/subfolder");
      testNotFound(driver, "/forge/images//image");
      testNotFound(driver, "/forge/images/a/image");
      testNotFound(driver, "/forge/images/2");
      testNotFound(driver, "/forge/images/2/");
      testNotFound(driver, "/forge/images/2/*");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayView() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testMayViewImage(driver, IMAGE_IN_ROOT);
      testMayViewImage(driver, IMAGE_IN_FOLDER);
      testMayViewImage(driver, IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayEdit() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testMayViewImage(driver, IMAGE_IN_ROOT);
      testMayViewImage(driver, IMAGE_IN_FOLDER);
      testMayViewImage(driver, IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }

  private void testMayViewImage(RemoteWebDriver driver, String path) {
    driver.get(getAppUrl() + path);
    assertEquals(1, driver.findElements(By.cssSelector(".forge-image-container img")).size());
  }
  
}
