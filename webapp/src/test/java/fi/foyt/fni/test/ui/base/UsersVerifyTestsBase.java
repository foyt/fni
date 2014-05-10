package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UsersVerifyTestsBase extends AbstractUITest {

  private static final Long USER_ID = 2048l;
  private static final String USER_EMAIL = "verify.test@foyt.fi";

  @Test
  public void testInvalidKeyTest() {
    getWebDriver().get(getAppUrl() + "/users/verify/bogus");
    assertEquals("Invalid Verification Key. Perhaps You Have Already Clicked This Link Before", getWebDriver().findElement(By.cssSelector(".jsf-messages-container li.error span")).getText());
  }

  @Test
  public void testCorrectKeyTest() throws Exception {
    createUser(USER_ID, "Reset", "Test", USER_EMAIL, "pass", "en_US", "GRAVATAR", "USER", false);
    try {
      getWebDriver().get(getAppUrl() + "/login");

      getWebDriver().findElement(By.cssSelector(".user-login-login-panel input[type='text']")).sendKeys(USER_EMAIL);
      getWebDriver().findElement(By.cssSelector(".user-login-login-panel input[type='password']")).sendKeys("pass");
      getWebDriver().findElement(By.cssSelector(".user-login-login-panel input[type='submit']")).click();

      new WebDriverWait(getWebDriver(), 10).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".jsf-messages-container li.warning span")));

      assertEquals("You Have Not Confirmed Your E-email Address", getWebDriver().findElement(By.cssSelector(".jsf-messages-container li.warning span")).getText());

      String key = UUID.randomUUID().toString();
      executeSql("insert into UserVerificationKey (id, created, value, user_id) values (?, ?, ?, ?)", USER_ID, new Date(), key, USER_ID);
      try {
        getWebDriver().get(getAppUrl() + "/users/verify/" + key);
        loginInternal(getWebDriver(), USER_EMAIL, "pass");
      } finally {
        executeSql("delete from UserVerificationKey where id = ?", USER_ID);
      }

    } finally {
      deleteUser(USER_ID);
    }
  }

}
