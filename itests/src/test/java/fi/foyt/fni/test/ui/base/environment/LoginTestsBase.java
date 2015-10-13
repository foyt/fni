package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic", 
    before = {"basic-users-setup.sql"},
    after = {"basic-users-teardown.sql"}
  )
})
public class LoginTestsBase extends AbstractUITest {
  
  @Before
  public void baseSetUp() throws Exception {
    createOAuthSettings();
  }

  @After
  public void baseTearDown() throws Exception {
    purgeOAuthSettings();
  }

  @Test
  public void testTitle() {
    testTitle("/login", "Login", true);
  }

  @Test
  @SqlSets ("basic")
  public void testInternal() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
  }

  @Test
  @SqlSets ("basic")
  public void testFacebook() {
    loginFacebook();
  }

  @Test
  @SqlSets ("basic")
  public void testGoogle() {
    loginGoogle();
  }

  @Test
  public void testRegisterMandatories() {
    acceptCookieDirective();
    getWebDriver().get(getAppUrl(true) + "/login/");
    getWebDriver().findElement(By.cssSelector(".user-register-button")).submit();
    waitForNotification(getWebDriver());
    assertEquals("First Name Is Required", getWebDriver().findElement(By.cssSelector(".notifications-container .notification-error:nth-child(1)")).getText());
    assertEquals("Last Name Is Required", getWebDriver().findElement(By.cssSelector(".notifications-container .notification-error:nth-child(2)")).getText());
    assertEquals("Email Is Required", getWebDriver().findElement(By.cssSelector(".notifications-container .notification-error:nth-child(3)")).getText());
  }

  @Test
  public void testRegisterPasswordMismatch() {
    acceptCookieDirective();
    
    getWebDriver().get(getAppUrl(true) + "/login/");
    
    getWebDriver().findElement(By.cssSelector(".user-register-first-name")).sendKeys("Ärri");
    getWebDriver().findElement(By.cssSelector(".user-register-last-name")).sendKeys("Pörri");
    getWebDriver().findElement(By.cssSelector(".user-register-email")).sendKeys("register.tester@foyt.fi");
    getWebDriver().findElement(By.cssSelector(".user-register-password1")).sendKeys("qwe");
    getWebDriver().findElement(By.cssSelector(".user-register-password2")).sendKeys("asd");
    getWebDriver().findElement(By.cssSelector(".user-register-button")).click();
    
    waitForNotification(getWebDriver());
    assertNotification(getWebDriver(), "warning", "Passwords Do Not Match");
  }

  @Test
  public void testRegister() throws MessagingException {
    acceptCookieDirective(getWebDriver());
    
    GreenMail greenMail = startSmtpServer();
    try {
      getWebDriver().get(getAppUrl(true) + "/login/");
      
      getWebDriver().findElement(By.cssSelector(".user-register-first-name")).sendKeys("Ärri");
      getWebDriver().findElement(By.cssSelector(".user-register-last-name")).sendKeys("Pörri");
      getWebDriver().findElement(By.cssSelector(".user-register-email")).sendKeys("register.tester@foyt.fi");
      getWebDriver().findElement(By.cssSelector(".user-register-password1")).sendKeys("qwe");
      getWebDriver().findElement(By.cssSelector(".user-register-password2")).sendKeys("qwe");
      getWebDriver().findElement(By.cssSelector(".user-register-button")).click();
      
      waitForNotification(getWebDriver());
      assertNotificationStartsWith(getWebDriver(), "info", "Verification Email Has Been Sent");

      assertEquals(1, greenMail.getReceivedMessages().length);

      String mailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Welcome to Forge & Illusion", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(mailBody, StringUtils.startsWithIgnoreCase(mailBody, "Welcome to the world of"));
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  public void testResetPasswordIncorrectEmail() {
    getWebDriver().get(getAppUrl(true) + "/login/");
    
    getWebDriver().findElement(By.cssSelector(".users-login-forgot-password-link")).click();

    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".ui-dialog")));

    assertEquals("Forgot password", getWebDriver().findElement(By.cssSelector(".ui-dialog-title")).getText());
    assertEquals("Enter your email address to the field below and we will send you a password reset link", getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog p")).getText());
    
    getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog input[name=\"email\"]")).sendKeys("nonexisting@foyt.fi");
    getWebDriver().findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();

    waitForNotification(getWebDriver());
    assertNotification(getWebDriver(), "warning", "User Could Not Be Found By Given E-mail");
  }
  
  @Test
  @SqlSets ("basic")
  public void testResetPasswordInvalidEmail() {
    getWebDriver().get(getAppUrl(true) + "/login/");
    
    getWebDriver().findElement(By.cssSelector(".users-login-forgot-password-link")).click();

    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".ui-dialog")));

    assertEquals("Forgot password", getWebDriver().findElement(By.cssSelector(".ui-dialog-title")).getText());
    assertEquals("Enter your email address to the field below and we will send you a password reset link", getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog p")).getText());
    
    getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog input[name=\"email\"]")).sendKeys("invalidaddress");
    getWebDriver().findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();
    
    waitForNotification(getWebDriver());
    assertNotification(getWebDriver(), "warning", "User Could Not Be Found By Given E-mail");
  }
  
  @Test
  @SqlSets ("basic")
  public void testResetPassword() throws MessagingException {
    GreenMail greenMail = startSmtpServer();
    try {
      getWebDriver().get(getAppUrl(true) + "/login/");
      
      getWebDriver().findElement(By.cssSelector(".users-login-forgot-password-link")).click();

      new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".ui-dialog")));

      assertEquals("Forgot password", getWebDriver().findElement(By.cssSelector(".ui-dialog-title")).getText());
      assertEquals("Enter your email address to the field below and we will send you a password reset link", getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog p")).getText());
      
      getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog input[name=\"email\"]")).sendKeys("user@foyt.fi");
      getWebDriver().findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();
      
      waitForNotification(getWebDriver());
      assertNotificationStartsWith(getWebDriver(), "info", "Password reset e-mail has been sent into user@foyt.fi. Click link on the e-mail to reset your password.");
      
      assertEquals(1, greenMail.getReceivedMessages().length);
      String mailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Forge & Illusion password reset", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(mailBody, StringUtils.startsWithIgnoreCase(mailBody, "<p>You have requested for password reset"));
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ("basic")
  public void testMissingInformation() {
    loginInternal(getWebDriver(), "missinginfo@foyt.fi", "pass");
    waitForUrlMatches(getWebDriver(), ".*/editprofile.*");
    assertEquals("Edit Profile", getWebDriver().getTitle());
    waitForNotification(getWebDriver());
    assertNotificationStartsWith(getWebDriver(), "info", "Your profile is missing some required information, please fill the missing fields before continuing");
    
  }

}
