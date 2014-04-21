package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeUploadTestsIT extends AbstractUITest {
  
  public ForgeUploadTestsIT() {
    driver = new ChromeDriver();
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(driver, "/forge/upload");
  }
  
  private RemoteWebDriver driver; 
}
