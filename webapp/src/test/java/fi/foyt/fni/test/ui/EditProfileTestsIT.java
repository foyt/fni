package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class EditProfileTestsIT extends AbstractUITest {
  
  public EditProfileTestsIT() {
  }

  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/editprofile");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testGuest() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      driver.get(getAppUrl() + "/editprofile");
      assertEquals("Access Denied!", driver.getTitle());
    } finally {
      driver.close();
    }
  }
  
}
