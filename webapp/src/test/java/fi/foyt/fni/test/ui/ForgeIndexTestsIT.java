package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class ForgeIndexTestsIT extends AbstractUITest {
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testTitle(driver, "/forge/", "Forge");
    } finally {
      driver.close();
    }
  }
 
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/forge/");
    } finally {
      driver.close();
    }
  }
  
}
