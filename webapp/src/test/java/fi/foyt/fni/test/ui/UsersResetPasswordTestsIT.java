package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

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
    createUser(USER_ID, "Reset", "Test", USER_EMAIL, "pass", "en_US", "GRAVATAR", "USER");
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
      deleteUser(USER_ID);
    }
  }
  
}
