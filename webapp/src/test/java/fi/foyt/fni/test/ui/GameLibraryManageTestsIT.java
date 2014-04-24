package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class GameLibraryManageTestsIT extends AbstractUITest {
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testTitle(driver, "/gamelibrary/manage/", "Game Library - Management");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/gamelibrary/manage/", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testAccessDenied(driver, "/gamelibrary/manage/", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLibrarian() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      driver.get(getAppUrl(true) + "/gamelibrary/manage/");
      assertEquals("Game Library - Management", driver.getTitle());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAdmin() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      driver.get(getAppUrl(true) + "/gamelibrary/manage/");
      assertEquals("Game Library - Management", driver.getTitle());
    } finally {
      driver.close();
    }
  }
  
}
