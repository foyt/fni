package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeVectorImageTestsIT extends AbstractUITest {
 
  private static final String VECTOR_IMAGE_IN_ROOT = "/forge/vectorimages/2/vectorimage";
  private static final String VECTOR_IMAGE_IN_FOLDER = "/forge/vectorimages/2/folder/vectorimage_in_folder";
  private static final String VECTOR_IMAGE_IN_SUBFOLDER = "/forge/vectorimages/2/folder/subfolder/vectorimage_in_subfolder";
    
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, VECTOR_IMAGE_IN_ROOT);
      testLoginRequired(driver, VECTOR_IMAGE_IN_FOLDER);
      testLoginRequired(driver, VECTOR_IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testForbidden() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, VECTOR_IMAGE_IN_ROOT);
      testAccessDenied(driver, VECTOR_IMAGE_IN_FOLDER);
      testAccessDenied(driver, VECTOR_IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testNotFound(driver, "/forge/vectorimages/2/folder/subfolder");
      testNotFound(driver, "/forge/vectorimages//image");
      testNotFound(driver, "/forge/vectorimages/a/image");
      testNotFound(driver, "/forge/vectorimages/2");
      testNotFound(driver, "/forge/vectorimages/2/");
      testNotFound(driver, "/forge/vectorimages/2/*");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayView() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testMayViewVectorImage(driver, VECTOR_IMAGE_IN_ROOT);
      testMayViewVectorImage(driver, VECTOR_IMAGE_IN_FOLDER);
      testMayViewVectorImage(driver, VECTOR_IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayEdit() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testMayEditVectorImage(driver, VECTOR_IMAGE_IN_ROOT);
      testMayEditVectorImage(driver, VECTOR_IMAGE_IN_FOLDER);
      testMayEditVectorImage(driver, VECTOR_IMAGE_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }

  private void testMayViewVectorImage(RemoteWebDriver driver, String path) {
    driver.get(getAppUrl() + path);
    assertEquals(1, driver.findElements(By.cssSelector(".forge-vector-image-container")).size());
    assertEquals(0, driver.findElements(By.cssSelector(".forge-vector-image-container .forge-vector-image-save")).size());
  }

  private void testMayEditVectorImage(RemoteWebDriver driver, String path) {
    driver.get(getAppUrl() + path);
    assertEquals(1, driver.findElements(By.cssSelector(".forge-vector-image-container")).size());
    assertEquals(1, driver.findElements(By.cssSelector(".forge-vector-image-container .forge-vector-image-save")).size());
  }
  
}
