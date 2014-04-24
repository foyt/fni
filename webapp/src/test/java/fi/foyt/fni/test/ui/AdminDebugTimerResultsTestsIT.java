package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class AdminDebugTimerResultsTestsIT extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testTitle(driver, "/admin/debug-timer-results", "Debug Timer Results");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/admin/debug-timer-results");
    } finally {
      driver.close();
    }
  }

  @Test
  public void testUnauthorized() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testAccessDenied(driver, "/admin/debug-timer-results");
    } finally {
      driver.close();
    }
  }
  
}
