package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class AboutTestsIT extends AbstractUITest {
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      testTitle(driver, "/about", "About Forge & Illusion");
    } finally {
      driver.close();
    }
  }
  
}
