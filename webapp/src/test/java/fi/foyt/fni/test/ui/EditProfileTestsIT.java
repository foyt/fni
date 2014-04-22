package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class EditProfileTestsIT extends AbstractUITest {
  
  public EditProfileTestsIT() {
    driver = new ChromeDriver();
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(driver, "/editprofile");
  }
  
  private RemoteWebDriver driver; 
}
