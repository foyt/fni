package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class AdminArchiveUserTestsIT extends AbstractUITest {
  
  private static final Long USER_ID = 1024l;
  
  @Before
  public void addTestData() throws Exception {
    executeSql(
      "insert into " +
      "  User (id, archived, firstName, lastName, locale, profileImageSource, registrationDate, role) " +
      "values " +
      "  (?, ?, ?, ?, ?, ?, ?, ?)", USER_ID, false, "Test", "Archiving", "en_US", "GRAVATAR", new Date(), "USER");
  }
  
  @After
  public void cleanTestData() throws Exception {
    executeSql("delete from User where id = ?", USER_ID);
  }
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/admin/archive-user/" + USER_ID);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testNotFound(driver, "/admin/archive-user");
      testNotFound(driver, "/admin/archive-user/");
      testNotFound(driver, "/admin/archive-user/");
      testNotFound(driver, "/admin/archive-user/-1");
      testNotFound(driver, "/admin/archive-user/2048");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testArchive() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      driver.get(getAppUrl() + "/admin/archive-user/" + USER_ID);
      assertEquals("Forge & Illusion", driver.getTitle());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testAccessDenied(driver, "/admin/archive-user/" + USER_ID);
    } finally {
      driver.close();
    }
  }
  
}
