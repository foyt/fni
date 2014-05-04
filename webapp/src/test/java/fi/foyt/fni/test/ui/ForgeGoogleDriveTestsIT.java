package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeGoogleDriveTestsIT extends AbstractUITest {
  
  private static final String GOOGLEDOC_IN_ROOT = "/forge/google-drive/2/googledoc";
  private static final String GOOGLEDOC_IN_FOLDER = "/forge/google-drive/2/folder/googledoc_in_folder";
  private static final String GOOGLEDOC_IN_SUBFOLDER = "/forge/google-drive/2/folder/subfolder/googledoc_in_subfolder";
    
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, GOOGLEDOC_IN_ROOT);
      testLoginRequired(driver, GOOGLEDOC_IN_FOLDER);
      testLoginRequired(driver, GOOGLEDOC_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testForbidden() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, GOOGLEDOC_IN_ROOT);
      testAccessDenied(driver, GOOGLEDOC_IN_FOLDER);
      testAccessDenied(driver, GOOGLEDOC_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayView() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testMayView(driver, GOOGLEDOC_IN_ROOT);
      testMayView(driver, GOOGLEDOC_IN_FOLDER);
      testMayView(driver, GOOGLEDOC_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayEdit() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testMayView(driver, GOOGLEDOC_IN_ROOT);
      testMayView(driver, GOOGLEDOC_IN_FOLDER);
      testMayView(driver, GOOGLEDOC_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }

  private void testMayView(RemoteWebDriver driver, String path) {
    driver.get(getAppUrl() + path);
    assertEquals(1, driver.findElements(By.cssSelector(".forge-google-drive-container iframe")).size());
  }
}
