package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
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
    executeSql("insert into User (id, archived, firstName, lastName, locale, profileImageSource, registrationDate, role) values (?, ?, ?, ?, ?, ?, ?, ?)", USER_ID, false, "Reset", "Test", "en_US", "GRAVATAR", new Date(), "USER");
    try {
      executeSql("insert into UserEmail (id, email, primaryEmail, user_id) values  (?, ?, ?, ?)", USER_ID, USER_EMAIL, true, USER_ID);
      try {
        executeSql("insert into InternalAuth (id, password, verified, user_id) values (?, ?, ?, ?)", USER_ID, DigestUtils.md5Hex("pass"), false, USER_ID);
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
          executeSql("delete from UserToken where userIdentifier_id in (select id from UserIdentifier where user_id = ?)", USER_ID);
          executeSql("delete from UserIdentifier where user_id = ?", USER_ID);
          executeSql("delete from InternalAuth where user_id = ?", USER_ID);
        }
      } finally {
        executeSql("delete from UserEmail where id = ?", USER_ID);
      }
    } finally {
      executeSql("delete from User where id = ?", USER_ID);
    }
  }
  
}
