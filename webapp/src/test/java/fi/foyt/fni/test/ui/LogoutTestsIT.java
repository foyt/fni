package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class LogoutTestsIT extends AbstractUITest {
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
      assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
      driver.get(getAppUrl() + "/logout");
      assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-account")).size());
      assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-login")).size());
    } finally {
      driver.close();
    }
  }
  
}
