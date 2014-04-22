package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class ForgeConnectDropboxTestsIT extends AbstractUITest {
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/forge/connect-dropbox");
    } finally {
      driver.close();
    }
  }
  
}
