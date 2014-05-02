package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginTestsIT extends AbstractUITest {
  
  @Before
  public void setup() throws Exception {
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_APIKEY", getFacebookApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_APISECRET", getFacebookApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_CALLBACKURL", getAppUrl() + "/login?return=1&loginMethod=FACEBOOK");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_APIKEY", getGoogleApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_APISECRET", getGoogleApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_CALLBACKURL", getAppUrl() + "/login?return=1&loginMethod=GOOGLE");
  }
  
  @After
  public void tearDown() throws Exception {
    executeSql("delete from SystemSetting where settingKey in ('FACEBOOK_APIKEY', 'FACEBOOK_APISECRET', 'FACEBOOK_CALLBACKURL', 'GOOGLE_APIKEY', 'GOOGLE_APISECRET', 'GOOGLE_CALLBACKURL')");
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
  public void testFacebook() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/login");
      driver.findElement(By.cssSelector("a[href=\"?loginMethod=FACEBOOK\"]")).click();
      driver.findElement(By.id("email")).sendKeys(getFacebookUsername());
      driver.findElement(By.id("pass")).sendKeys(getFacebookPassword());
      driver.findElement(By.name("login")).click();
      assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
      assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
    } finally {
      driver.close();
    }
  }

  @Test
  public void testGoogle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/login");
      driver.findElement(By.cssSelector("a[href=\"?loginMethod=GOOGLE\"]")).click();
      driver.findElement(By.name("Email")).sendKeys(getGoogleUsername());
      driver.findElement(By.name("Passwd")).sendKeys(getGooglePassword());
      driver.findElement(By.name("signIn")).click();
      assertEquals(1, driver.findElements(By.cssSelector(".menu-tools-account")).size());
      assertEquals(0, driver.findElements(By.cssSelector(".menu-tools-login")).size());
    } finally {
      driver.close();
    }
  }
  
}
