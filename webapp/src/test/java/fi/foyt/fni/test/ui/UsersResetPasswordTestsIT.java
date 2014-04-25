package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class UsersResetPasswordTestsIT extends AbstractUITest {
  
  private static final Long USER_ID = 1024l;
  private static final String USER_EMAIL = "reset.test@foyt.fi";
  
  @Test
  public void testInvalidKeyTest() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/users/resetpassword/bogus");
      
      WebElement form = driver.findElement(By.cssSelector(".user-resetpassword-panel form"));
      String formId = form.getAttribute("id");
      driver.findElement(By.name(formId + ":password1")).sendKeys("qwe");
      driver.findElement(By.name(formId + ":password2")).sendKeys("qwe");
      driver.findElement(By.cssSelector("input[type='submit']")).click();

      assertEquals("Invalid Password Reset Key. Perhaps You Have Already Used This Reset Link.", driver.findElement(By.cssSelector(".jsf-messages-container li.error span")).getText());
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
        executeSql("insert into InternalAuth (id, password, verified, user_id) values (?, ?, ?, ?)", USER_ID, DigestUtils.md5Hex("pass"), true, USER_ID);
        try {
          ChromeDriver driver = new ChromeDriver();
          try {
            loginInternal(driver, USER_EMAIL, "pass");
            logout(driver);
            
            String key = UUID.randomUUID().toString();
            
            executeSql("insert into PasswordResetKey (id, created, value, user_id) values (?, ?, ?, ?)", USER_ID, new Date(), key, USER_ID);
            try {
              driver.get(getAppUrl() + "/users/resetpassword/" + key);
              WebElement form = driver.findElement(By.cssSelector(".user-resetpassword-panel form"));
              String formId = form.getAttribute("id");
              driver.findElement(By.name(formId + ":password1")).sendKeys("qwe");
              driver.findElement(By.name(formId + ":password2")).sendKeys("qwe");
              driver.findElement(By.cssSelector("input[type='submit']")).click();
              
              loginInternal(driver, USER_EMAIL, "qwe");
            } finally {
              executeSql("delete from PasswordResetKey where id = ?", USER_ID);
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
