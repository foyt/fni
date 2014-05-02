package fi.foyt.fni.test.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginTestsIT extends AbstractUITest {
  
  @Before
  public void setup() throws Exception {
    createOAuthSettings();
  }
  
  @After
  public void tearDown() throws Exception {
    purgeOAuthSettings();
  }
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      testTitle(driver, "/login", "Login");
    } finally {
      driver.close();
    }
  }

  @Test
  public void testInternal() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user", "pass");
    } finally {
      driver.close();
    }
  }

  @Test
  public void testFacebook() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginFacebook(driver);
    } finally {
      driver.close();
    }
  }

  @Test
  public void testGoogle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginGoogle(driver);
    } finally {
      driver.close();
    }
  }
  
}
