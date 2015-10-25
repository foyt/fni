package fi.foyt.fni.test.ui.base.environment;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.ui.base.AbstractUITest;

public class UsersVerifyTestsBase extends AbstractUITest {

  private static final Long USER_ID = 2048l;
  private static final String USER_EMAIL = "verify.test@foyt.fi";

  @Test
  public void testInvalidKeyTest() {
    getWebDriver().get(getAppUrl() + "/users/verify/bogus");
    waitForNotification(getWebDriver());
    assertNotification("error", "Invalid Verification Key. Perhaps You Have Already Clicked This Link Before");
  }

  @Test
  public void testCorrectKeyTest() throws Exception {
    acceptCookieDirective();
    
    createUser(USER_ID, "Reset", "Test", USER_EMAIL, "pass", "en_US", "GRAVATAR", "USER", false);
    try {
      getWebDriver().get(getAppUrl() + "/login");

      getWebDriver().findElement(By.cssSelector(".user-login-email")).sendKeys(USER_EMAIL);
      getWebDriver().findElement(By.cssSelector(".user-login-password")).sendKeys("pass");
      getWebDriver().findElement(By.cssSelector(".user-login-button")).click();

      waitForNotification(getWebDriver());
      assertNotification("warning", "You Have Not Confirmed Your E-email Address");

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
