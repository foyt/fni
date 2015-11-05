package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

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
    navigate("/login/", true);
    waitTitle("Login");
    clickSelector(".user-register-button");
    waitForNotification();
    assertSelectorText(".notifications-container .notification-error:nth-child(1)", "First Name Is Required", true, true);
    assertSelectorText(".notifications-container .notification-error:nth-child(2)", "Last Name Is Required", true, true);
    assertSelectorText(".notifications-container .notification-error:nth-child(3)", "Email Is Required", true, true);
  }

  @Test
  public void testRegisterPasswordMismatch() {
    acceptCookieDirective();
    navigate("/login/", true);
    
    waitAndSendKeys(".user-register-first-name", "Ärri");
    waitAndSendKeys(".user-register-last-name", "Pörri");
    waitAndSendKeys(".user-register-email", "register.tester@foyt.fi");
    waitAndSendKeys(".user-register-password1", "qwe");
    waitAndSendKeys(".user-register-password2", "asd");
    waitAndClick(".user-register-button");
    
    waitForNotification();
    assertNotification("warning", "Passwords Do Not Match");
  }

  @Test
  public void testRegister() throws MessagingException {
    acceptCookieDirective(getWebDriver());
    
    GreenMail greenMail = startSmtpServer();
    try {
      navigate("/login/", true);
      
      waitAndSendKeys(".user-register-first-name", "Ärri");
      waitAndSendKeys(".user-register-last-name", "Pörri");
      waitAndSendKeys(".user-register-email", "register.tester@foyt.fi");
      waitAndSendKeys(".user-register-password1", "qwe");
      waitAndSendKeys(".user-register-password2", "qwe");
      waitAndClick(".user-register-button");
      
      waitForNotification();
      assertNotificationStartsWith("info", "Verification Email Has Been Sent");

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
    navigate("/login/", true);
    waitTitle("Login");
    waitAndClick(".users-login-forgot-password-link");
    waitForSelectorPresent(".ui-dialog");
    assertSelectorText(".ui-dialog-title", "Forgot password", true, true);
    assertSelectorText(".users-forgot-password-dialog p", "Enter your email address to the field below and we will send you a password reset link", true, true);
    sendKeysSelector(".users-forgot-password-dialog input[name=\"email\"]", "nonexisting@foyt.fi");
    clickSelector(".ui-dialog-buttonpane .ok-button");
    waitForNotification();
    assertNotification("warning", "User Could Not Be Found By Given E-mail");
  }
  
  @Test
  @SqlSets ("basic")
  public void testResetPasswordInvalidEmail() {
    navigate("/login/", true);
    waitTitle("Login");

    waitAndClick(".users-login-forgot-password-link");
    waitForSelectorPresent(".ui-dialog");
    
    assertEquals("Forgot password", getWebDriver().findElement(By.cssSelector(".ui-dialog-title")).getText());
    assertEquals("Enter your email address to the field below and we will send you a password reset link", getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog p")).getText());
    
    waitAndSendKeys(".users-forgot-password-dialog input[name=\"email\"]", "invalidaddress");
    waitAndClick(".ui-dialog-buttonpane .ok-button");
    
    waitForNotification();
    assertNotification("warning", "User Could Not Be Found By Given E-mail");
  }
  
  @Test
  @SqlSets ("basic")
  public void testResetPassword() throws MessagingException {
    GreenMail greenMail = startSmtpServer();
    try {
      navigate("/login/", true);
      waitTitle("Login");
      waitAndClick(".users-login-forgot-password-link");
      waitForSelectorPresent(".ui-dialog");

      assertEquals("Forgot password", getWebDriver().findElement(By.cssSelector(".ui-dialog-title")).getText());
      assertEquals("Enter your email address to the field below and we will send you a password reset link", getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog p")).getText());
      
      getWebDriver().findElement(By.cssSelector(".users-forgot-password-dialog input[name=\"email\"]")).sendKeys("user@foyt.fi");
      getWebDriver().findElement(By.cssSelector(".ui-dialog-buttonpane .ok-button")).click();
      
      waitForNotification();
      assertNotificationStartsWith("info", "Password reset e-mail has been sent into user@foyt.fi. Click link on the e-mail to reset your password.");
      
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
    waitForNotification();
    assertNotificationStartsWith("info", "Your profile is missing some required information, please fill the missing fields before continuing");
    
  }

}
