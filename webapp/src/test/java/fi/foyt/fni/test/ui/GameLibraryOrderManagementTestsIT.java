package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class GameLibraryOrderManagementTestsIT extends AbstractUITest {
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/gamelibrary/ordermanagement/", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testAccessDenied(driver, "/gamelibrary/ordermanagement/", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLibrarian() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testTitle(driver, "/gamelibrary/ordermanagement/", "Game Library - Order Management");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAdmin() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testTitle(driver, "/gamelibrary/ordermanagement/", "Game Library - Order Management");
    } finally {
      driver.close();
    }
  }
  
}
