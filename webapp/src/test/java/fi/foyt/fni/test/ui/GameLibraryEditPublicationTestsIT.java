package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class GameLibraryEditPublicationTestsIT extends AbstractUITest {
  
  private static final Long PUBLICATION_ID = 1l;
  private static final String TEST_URL = "/gamelibrary/manage/" + PUBLICATION_ID + "/edit";
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, TEST_URL, true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testAccessDenied(driver, TEST_URL, true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testNotFound(driver, "/gamelibrary/manage/¨/edit");
      testNotFound(driver, "/gamelibrary/manage/-1/edit");
      testNotFound(driver, "/gamelibrary/manage//edit");
      testNotFound(driver, "/gamelibrary/manage/asd/edit");
    } finally {
      driver.close();
    }
  }

  @Test
  public void testLibrarian() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testTitle(driver, TEST_URL, "Edit Publication: Test Book #1");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAdmin() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testTitle(driver, TEST_URL, "Edit Publication: Test Book #1");
    } finally {
      driver.close();
    }
  }
  
}
