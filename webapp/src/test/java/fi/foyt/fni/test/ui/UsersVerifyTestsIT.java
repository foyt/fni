package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UsersVerifyTestsIT extends AbstractUITest {
  
  private static final Long USER_ID = 2048l;
  private static final String USER_EMAIL = "verify.test@foyt.fi";
  
  @Test
  public void testInvalidKeyTest() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/users/verify/bogus");
      assertEquals("Invalid Verification Key. Perhaps You Have Already Clicked This Link Before", driver.findElement(By.cssSelector(".jsf-messages-container li.error span")).getText());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testCorrectKeyTest() throws Exception {
    createUser(USER_ID, "Reset", "Test", USER_EMAIL, "pass", "en_US", "GRAVATAR", "USER", false);
    try {
      ChromeDriver driver = new ChromeDriver();
      try {
        driver.get(getAppUrl() + "/login");
        
        driver.findElement(By.cssSelector(".user-login-login-panel input[type='text']"))
          .sendKeys(USER_EMAIL);
        driver.findElement(By.cssSelector(".user-login-login-panel input[type='password']"))
          .sendKeys("pass");
        driver.findElement(By.cssSelector(".user-login-login-panel input[type='submit']"))
          .click();
        
        new WebDriverWait(driver, 10)
          .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".jsf-messages-container li.warning span")));
        
        assertEquals("You Have Not Confirmed Your E-email Address", driver.findElement(By.cssSelector(".jsf-messages-container li.warning span")).getText());

        String key = UUID.randomUUID().toString();
        executeSql("insert into UserVerificationKey (id, created, value, user_id) values (?, ?, ?, ?)", USER_ID, new Date(), key, USER_ID);
        try {
          driver.get(getAppUrl() + "/users/verify/" + key);
          loginInternal(driver, USER_EMAIL, "pass");
        } finally {
          executeSql("delete from UserVerificationKey where id = ?", USER_ID);
        }
        
      } finally {
        driver.close();
      }
    } finally {
      deleteUser(USER_ID);
    }
  }
  
}
