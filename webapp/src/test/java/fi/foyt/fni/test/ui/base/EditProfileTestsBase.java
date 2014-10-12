package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

public class EditProfileTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/editprofile", "Edit Profile");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/editprofile");
  }

  @Test
  public void testGuest() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/editprofile");
  }

  @Test
  public void testUser() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }

  @Test
  public void testLibrarian() throws Exception {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }

  @Test
  public void testAdministrator() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }
  
  @Test
  public void testChangeInternalPasswordRequired() throws Exception {
    createUser(7l, "Password", "Change", "passchange@foyt.fi", "oldpass", "en", "GRAVATAR", "USER");
    try {
      loginInternal(getWebDriver(), "passchange@foyt.fi", "oldpass");
      getWebDriver().get(getAppUrl() + "/editprofile");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password")).click();
      waitForElementVisible(getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container")));
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container input[type=\"submit\"]")).click();
      waitForNotification(getWebDriver());
      assertNotification(getWebDriver(), "warning", "Password is required");
    } finally {
      deleteUser(7l);
    }
  } 

  @Test
  public void testChangeInternalPasswordNoMatch() throws Exception {
    createUser(7l, "Password", "Change", "passchange@foyt.fi", "oldpass", "en", "GRAVATAR", "USER");
    try {
      loginInternal(getWebDriver(), "passchange@foyt.fi", "oldpass");
      getWebDriver().get(getAppUrl() + "/editprofile");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password")).click();
      waitForElementVisible(getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container")));

      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-password1")).sendKeys("qwe");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-password2")).sendKeys("asd");
      
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container input[type=\"submit\"]")).click();
      waitForNotification(getWebDriver());
      assertNotification(getWebDriver(), "warning", "Passwords do not match");
    } finally {
      deleteUser(7l);
    }
  } 
  
  @Test
  public void testChangeInternalPassword() throws Exception {
    createUser(7l, "Password", "Change", "passchange@foyt.fi", "oldpass", "en", "GRAVATAR", "USER");
    try {
      loginInternal(getWebDriver(), "passchange@foyt.fi", "oldpass");
      getWebDriver().get(getAppUrl() + "/editprofile");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password")).click();
      waitForElementVisible(getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container")));

      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-password1")).sendKeys("qwe");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-password2")).sendKeys("qwe");
      
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container input[type=\"submit\"]")).click();
      waitForNotification(getWebDriver());
      assertNotification(getWebDriver(), "info", "Password Changed");

      loginInternal(getWebDriver(), "passchange@foyt.fi", "qwe");
    } finally {
      deleteUser(7l);
    }
  } 
    
}
