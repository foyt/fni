package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class UserProfileTestsIT extends AbstractUITest {
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      testTitle(driver, "/profile/1", "User Profile");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testNotFound(driver, "/profile/~");
      testNotFound(driver, "/profile/12345");
      testNotFound(driver, "/profile/-1");
      testNotFound(driver, "/profile/");
      testNotFound(driver, "/profile/asd");
    } finally {
      driver.close();
    }
  }
  
}
