package fi.foyt.fni.test.ui.base;

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
    testTitle(getWebDriver(), "/login", "Login", true);
  }

  @Test
  public void testInternal() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
  }

  @Test
  public void testFacebook() {
    loginFacebook(getWebDriver());
  }

  @Test
  public void testGoogle() {
    loginGoogle(getWebDriver());
  }

  @Test
  public void testRegisterMandatories() {
    getWebDriver().get(getAppUrl(true) + "/login/");
    getWebDriver().findElement(By.cssSelector(".user-register-button")).submit();
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".jsf-messages-container li:nth-child(1).error span")));
    assertEquals("First Name Is Required", getWebDriver().findElement(By.cssSelector(".jsf-messages-container li:nth-child(1).error span")).getText());
    assertEquals("Last Name Is Required", getWebDriver().findElement(By.cssSelector(".jsf-messages-container li:nth-child(2).error span")).getText());
    assertEquals("Email Is Required", getWebDriver().findElement(By.cssSelector(".jsf-messages-container li:nth-child(3).error span")).getText());
  }

  @Test
  public void testRegisterPasswordMismatch() {
    getWebDriver().get(getAppUrl(true) + "/login/");
    
    getWebDriver().findElement(By.cssSelector(".user-register-first-name")).sendKeys("Ärri");
    getWebDriver().findElement(By.cssSelector(".user-register-last-name")).sendKeys("Pörri");
    getWebDriver().findElement(By.cssSelector(".user-register-email")).sendKeys("register.tester@foyt.fi");
    getWebDriver().findElement(By.cssSelector(".user-register-password1")).sendKeys("qwe");
    getWebDriver().findElement(By.cssSelector(".user-register-password2")).sendKeys("asd");
    getWebDriver().findElement(By.cssSelector(".user-register-button")).click();
    
    new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".jsf-messages-container li span")));
    assertEquals("Passwords Do Not Match", getWebDriver().findElement(By.cssSelector(".jsf-messages-container li span")).getText());
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
      
      new WebDriverWait(getWebDriver(), 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".jsf-messages-container li:nth-child(1).info span")));
      String notification = getWebDriver().findElement(By.cssSelector(".jsf-messages-container li:nth-child(1).info span")).getText();
      assertTrue(notification, StringUtils.startsWithIgnoreCase(notification, "Verification Email Has Been Sent"));
      assertEquals(1, greenMail.getReceivedMessages().length);

      String mailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Welcome to Forge & Illusion", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(mailBody, StringUtils.startsWithIgnoreCase(mailBody, "Welcome to the world of"));
    } finally {
      greenMail.stop();
    } 
  }

}
