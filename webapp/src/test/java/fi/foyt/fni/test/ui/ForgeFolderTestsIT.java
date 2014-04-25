package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeFolderTestsIT extends AbstractUITest {
 
  private static final String FOLDER = "/forge/folders/2/folder";
  private static final String SUBFOLDER = "/forge/folders/2/folder/subfolder";
    
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, FOLDER);
      testLoginRequired(driver, SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testForbidden() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, FOLDER);
      testAccessDenied(driver, SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testNotFound(driver, "/forge/folders/2/folder/image_in_folder");
      testNotFound(driver, "/forge/folders//folder");
      testNotFound(driver, "/forge/folders/a/folder");
      testNotFound(driver, "/forge/folders/2");
      testNotFound(driver, "/forge/folders/2/");
      testNotFound(driver, "/forge/folders/2/*");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayView() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testMayViewFolder(driver, FOLDER);
      testMayViewFolder(driver, SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayEdit() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testMayViewFolder(driver, FOLDER);
      testMayViewFolder(driver, SUBFOLDER);
    } finally {
      driver.close();
    }
  }

  private void testMayViewFolder(RemoteWebDriver driver, String path) {
    driver.get(getAppUrl() + path);
    assertEquals("Forge", driver.getTitle());
  }
  
}
