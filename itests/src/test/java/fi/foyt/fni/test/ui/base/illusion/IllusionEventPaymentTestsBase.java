package fi.foyt.fni.test.ui.base.illusion;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.rest.illusion.model.IllusionEventParticipant;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlParam;
import fi.foyt.fni.test.SqlSet;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractIllusionUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql"  }),
  @DefineSqlSet (id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet (id = "illusion-paid-events", before = {"illusion-event-oai-paid-setup.sql"}, after = {"illusion-event-oai-paid-teardown.sql"} ),
  @DefineSqlSet (id = "illusion-paid-events-custom", before = {"illusion-event-oai-paid-custom-setup.sql"}, after = {"illusion-event-oai-paid-custom-teardown.sql"} ),
  @DefineSqlSet (id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql"),
  @DefineSqlSet(id = "illusion-event-form", 
    before = {"illusion-event-form-setup.sql" }, 
    after = {"illusion-event-form-teardown.sql"}, params = {
      @SqlParam (name = "id", value = "2"), 
      @SqlParam (name = "data", value = "{\"schema\":{\"type\":\"object\",\"required\":false,\"properties\":{\"firstname\":{\"type\":\"string\",\"required\":true},\"lastname\":{\"type\":\"string\",\"required\":true},\"email\":{\"type\":\"string\",\"required\":true}}},\"options\":{\"type\":\"object\",\"fields\":{\"firstname\":{\"label\":\"First name\",\"type\":\"text\",\"id\":\"firstname\",\"order\":\"0\"},\"lastname\":{\"label\":\"Last name\",\"type\":\"text\",\"id\":\"lastname\",\"order\":\"1\"},\"email\":{\"label\":\"Email\",\"type\":\"email\",\"id\":\"email\",\"order\":\"2\"}}}}") 
    }
  )
})
public class IllusionEventPaymentTestsBase extends AbstractIllusionUITest {
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-paid-events"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/open/payment/");
    testNotFound("/illusion/event/noevent/payment");
    testNotFound("/illusion/event/noevent//payment");
    testNotFound("/illusion/event/noevent/*/payment");
    testNotFound("/illusion/event/1/payment");
    testNotFound("/illusion/event///payment");
    testNotFound("/illusion/event//*/payment");
    testNotFound("/illusion/event/~/payment");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-paid-events"})
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/invite/payment");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-paid-events"})
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/open/payment");
  }
  
  @Test
  @SqlSets (
    sets = {
      @SqlSet (id = "basic-users"),
      @SqlSet (id = "user-client"),
      @SqlSet (id = "illusion-basic"),
      @SqlSet (id = "illusion-paid-events"),
      @SqlSet (id = "illusion-event-form", params = {
        @SqlParam (name = "eventId", value = "3")
      })
  })
  public void testApprovePaymentNotLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      navigate("/illusion/event/approve");
      
      // User fills the registration form
      
      waitAndClick(".illusion-event-join-button");
      waitAndSendKeys("input[name='firstname']", "Automatic");
      waitAndSendKeys("input[name='lastname']", "Tester");
      waitAndSendKeys("input[name='email']", "automatic.tester@example.com");
      waitAndClick(".alpaca-form-button-register");
      waitForNotification();
      assertNotification("warning", "Waiting for event organizer to accept your request...");
      
      assertLoggedIn();
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Approve", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Automatic,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Approve", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));
      
      logout();
      greenMail.reset();

      // Simulate organizer acceptance by changing role
      updateEventParticipantRole(3l, "automatic.tester@example.com", IllusionEventParticipantRole.WAITING_PAYMENT);

      // User receives payment link
      assertEquals(1, greenMail.getReceivedMessages().length);
      String acceptMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertTrue(StringUtils.contains(acceptMailBody, "Event organizer has accepted your request"));
      
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(acceptMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Automatic");
      assertSelectorValue(".payerLastName", "Tester");
      assertSelectorValue(".payerEmail", "automatic.tester@example.com");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);
      
      assertEquals("automatic.tester@example.com", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Approve"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Approve"), greenMail.getReceivedMessages()[1].getSubject());
    } finally {
      greenMail.stop();
    }    
  }

  @Test
  @SqlSets (
    sets = {
      @SqlSet (id = "basic-users"),
      @SqlSet (id = "user-client"),
      @SqlSet (id = "illusion-basic"),
      @SqlSet (id = "illusion-paid-events"),
      @SqlSet (id = "illusion-event-form", params = {
        @SqlParam (name = "eventId", value = "3")
      })
    }
  )
  public void testApprovePaymentLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      
      navigate("/illusion/event/approve");
      
      // User fills the registration form
      waitAndClick(".illusion-event-join-button");

      waitForSelectorVisible("input[name='firstname']");
      assertSelectorValue("input[name='firstname']", "Test");
      assertSelectorValue("input[name='lastname']", "User");
      assertSelectorValue("input[name='email']", "user@foyt.fi");
      
      waitAndClick(".alpaca-form-button-register");
      waitForNotification();
      assertNotification("warning", "Waiting for event organizer to accept your request...");
      
      assertLoggedIn();
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Approve", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Test,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Approve", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));
      
      logout();
      greenMail.reset();

      // Simulate organizer acceptance by changing role
      updateEventParticipantRole(3l, "user@foyt.fi", IllusionEventParticipantRole.WAITING_PAYMENT);

      loginInternal("user@foyt.fi", "pass");

      // User receives payment link
      assertEquals(1, greenMail.getReceivedMessages().length);
      String acceptMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertTrue(StringUtils.contains(acceptMailBody, "Event organizer has accepted your request"));
      
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(acceptMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Test");
      assertSelectorValue(".payerLastName", "User");
      assertSelectorValue(".payerEmail", "user@foyt.fi");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);
      
      assertEquals("user@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Approve"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Approve"), greenMail.getReceivedMessages()[1].getSubject());
    } finally {
      greenMail.stop();
    }    
  }
  
  @Test
  @SqlSets (
    sets = {
      @SqlSet (id = "basic-users"),
      @SqlSet (id = "illusion-basic"),
      @SqlSet (id = "illusion-paid-events"),
      @SqlSet (id = "illusion-event-form", params = {
        @SqlParam (name = "eventId", value = "2")
      })
    }
  )
  public void testOpenPaymentNotLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      navigate("/illusion/event/open");
      
      // User fills the registration form
      
      waitAndClick(".illusion-event-join-button");
      waitAndSendKeys("input[name='firstname']", "Automatic");
      waitAndSendKeys("input[name='lastname']", "Tester");
      waitAndSendKeys("input[name='email']", "automatic.tester@example.com");
      waitAndClick(".alpaca-form-button-register");
      
      waitForSelectorPresent(".menu-tools-account");
      assertLoggedIn();
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Automatic,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));
      
      // Participant receives payment link in registration mail
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(registrantMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Automatic");
      assertSelectorValue(".payerLastName", "Tester");
      assertSelectorValue(".payerEmail", "automatic.tester@example.com");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");

      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);
      
      assertEquals("automatic.tester@example.com", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Open"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Open"), greenMail.getReceivedMessages()[1].getSubject());
    } finally {
      greenMail.stop();
    }    
  }
  
  @Test
  @SqlSets (
    sets = {
      @SqlSet (id = "basic-users"),
      @SqlSet (id = "illusion-basic"),
      @SqlSet (id = "illusion-paid-events"),
      @SqlSet (id = "illusion-event-form", params = {
        @SqlParam (name = "eventId", value = "2")
      })
    }
  )
  public void testOpenPaymentLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      
      navigate("/illusion/event/open");
      
      // User fills the registration form
      
      waitAndClick(".illusion-event-join-button");
      waitForSelectorVisible("input[name='firstname']");
      assertSelectorValue("input[name='firstname']", "Test");
      assertSelectorValue("input[name='lastname']", "User");
      assertSelectorValue("input[name='email']", "user@foyt.fi");
      waitAndClick(".alpaca-form-button-register");
      
      waitForSelectorPresent(".menu-tools-account");
      assertLoggedIn();
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Test,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));
      
      // Participant receives payment link in registration mail
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(registrantMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Test");
      assertSelectorValue(".payerLastName", "User");
      assertSelectorValue(".payerEmail", "user@foyt.fi");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);
      
      assertEquals("user@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Open"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Open"), greenMail.getReceivedMessages()[1].getSubject());
    } finally {
      greenMail.stop();
    }    
  }
  
  @Test
  @SqlSets (
    sets = {
      @SqlSet (id = "basic-users"),
      @SqlSet (id = "user-client"),
      @SqlSet (id = "illusion-basic"),
      @SqlSet (id = "illusion-paid-events"),
      @SqlSet (id = "illusion-event-form", params = {
        @SqlParam (name = "eventId", value = "4")
      })
    }
  )
  public void testInvitePaymentNotLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      createEventParticipant(4l, 2l, IllusionEventParticipantRole.INVITED);
      loginInternal("user@foyt.fi", "pass");
      
      navigate("/illusion/event/invite/dojoin");

      // User fills the registration form
      waitForSelectorVisible("input[name='firstname']");
      assertSelectorValue("input[name='firstname']", "Test");
      assertSelectorValue("input[name='lastname']", "User");
      assertSelectorValue("input[name='email']", "user@foyt.fi");
      waitAndClick(".alpaca-form-button-register");
      waitMailsReceived(greenMail, 2);
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Invite Only", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Test,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Invite Only", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));

      // Participant receives payment link in registration mail
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(registrantMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Test");
      assertSelectorValue(".payerLastName", "User");
      assertSelectorValue(".payerEmail", "user@foyt.fi");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);

      assertEquals("user@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Invite Only"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Invite Only"), greenMail.getReceivedMessages()[1].getSubject());
      
    } finally {
      greenMail.stop();
    }    
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-paid-events"})
  public void testFormlessOpenPaymentLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      
      navigate("/illusion/event/open");
      
      // User fills the registration form
      waitAndClick(".illusion-event-join-button");

      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Test,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));

      // Participant receives payment link in registration mail
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(registrantMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      logout();
      greenMail.reset();

      loginInternal("user@foyt.fi", "pass");
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Test");
      assertSelectorValue(".payerLastName", "User");
      assertSelectorValue(".payerEmail", "user@foyt.fi");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);

      assertEquals("user@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Open"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Open"), greenMail.getReceivedMessages()[1].getSubject());
      
    } finally {
      greenMail.stop();
    }    
  }

  @Test
  @SqlSets ({"basic-users", "user-client", "illusion-basic", "illusion-paid-events"})
  public void testFormlessApprovedPaymentLoggedIn() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      
      navigate("/illusion/event/approve");
      
      // User fills the registration form
      waitAndClick(".illusion-event-join-button");
      waitForNotification();
      assertNotification("warning", "Waiting for event organizer to accept your request...");
      
      assertLoggedIn();
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Approve", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Test,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Approve", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));
      
      logout();
      greenMail.reset();

      // Simulate organizer acceptance by changing role
      updateEventParticipantRole(3l, "user@foyt.fi", IllusionEventParticipantRole.WAITING_PAYMENT);

      loginInternal("user@foyt.fi", "pass");

      // User receives payment link
      assertEquals(1, greenMail.getReceivedMessages().length);
      String acceptMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertTrue(StringUtils.contains(acceptMailBody, "Event organizer has accepted your request"));
      
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(acceptMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Test");
      assertSelectorValue(".payerLastName", "User");
      assertSelectorValue(".payerEmail", "user@foyt.fi");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);
      
      assertEquals("user@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Approve"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Approve"), greenMail.getReceivedMessages()[1].getSubject());
    } finally {
      greenMail.stop();
    }    
  }
  
  @Test
  @SqlSets (
    sets = {
      @SqlSet (id = "basic-users"),
      @SqlSet (id = "illusion-basic"),
      @SqlSet (id = "illusion-paid-events"),
      @SqlSet (id = "illusion-paid-events-custom"),
      @SqlSet (id = "illusion-event-form", params = {
        @SqlParam (name = "eventId", value = "2")
      })
    }
  )
  public void testOpenPaymentNotLoggedInCustomDomain() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      getWebDriver().get(getCustomEventUrl());
      
      // User fills the registration form
      
      waitAndClick(".illusion-event-join-button");
      waitAndSendKeys("input[name='firstname']", "Automatic");
      waitAndSendKeys("input[name='lastname']", "Tester");
      waitAndSendKeys("input[name='email']", "automatic.tester@example.com");
      waitAndClick(".alpaca-form-button-register");

      waitForSelectorPresent(".menu-tools-account");
      assertLoggedIn();
      
      assertEquals(2, greenMail.getReceivedMessages().length);

      String registrantMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[0].getSubject());
      assertTrue(registrantMailBody, StringUtils.startsWithIgnoreCase(registrantMailBody, "<p>Hi Automatic,"));

      String organizerMailBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[1]);
      assertEquals("Illusion - Registration to event Open", greenMail.getReceivedMessages()[1].getSubject());
      assertTrue(organizerMailBody, StringUtils.startsWithIgnoreCase(organizerMailBody, "<p>Hi Test,"));
      
      // Participant receives payment link in registration mail
      Pattern pattern = Pattern.compile("(.*)(<a href=\")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(registrantMailBody);
      assertTrue(matcher.matches());
      String paymentLink = matcher.group(3);
      assertNotNull(paymentLink);
      
      greenMail.reset();
      
      // User clicks the payment link and pays the sign-up fee
      getWebDriver().get(paymentLink);
      assertTrue(StringUtils.startsWith(paymentLink, String.format("http://%s", AbstractIllusionUITest.CUSTOM_EVENT_HOST)));
      
      waitForSelectorVisible(".payerCompany");
      assertSelectorValue(".payerFirstName", "Automatic");
      assertSelectorValue(".payerLastName", "Tester");
      assertSelectorValue(".payerEmail", "automatic.tester@example.com");
      waitAndSendKeys(".payerMobile", "+358 12 345 6789");
      waitAndSendKeys(".payerStreetAddress", "Tester street 123");
      waitAndSendKeys(".payerPostalCode", "12345");
      waitAndSendKeys(".payerPostalOffice", "Test");
      waitAndSendKeys(".notes", "This is an automated test");
      
      assertLoggedIn();

      scrollWaitAndClick(".proceed-to-payment");
      acceptPaytrailPayment();
      
      waitMailsReceived(greenMail, 2);
      assertEquals(2, greenMail.getReceivedMessages().length);
      
      assertEquals("automatic.tester@example.com", ((InternetAddress) greenMail.getReceivedMessages()[0].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment for event %s received", "Open"), greenMail.getReceivedMessages()[0].getSubject());
      
      assertEquals("admin@foyt.fi", ((InternetAddress) greenMail.getReceivedMessages()[1].getAllRecipients()[0]).getAddress());
      assertEquals(String.format("Payment received for event %s", "Open"), greenMail.getReceivedMessages()[1].getSubject());
      
      assertTrue(StringUtils.startsWith(getWebDriver().getCurrentUrl(), String.format("http://%s", AbstractIllusionUITest.CUSTOM_EVENT_HOST)));
      
    } finally {
      greenMail.stop();
    }    
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-paid-events", "illusion-paid-events-custom"})
  public void testCustomDomainLoginRedirect() throws Exception {
    getWebDriver().get(getCustomEventUrl() + "/payment");
    waitForSelectorPresent(".user-login-button");
  }
  
  private void updateEventParticipantRole(Long eventId, String email, IllusionEventParticipantRole role) throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    
    Response response = createRestClient()
      .get(String.format("/rest/illusion/events/{EVENTID}/participants?email=%s", email), eventId);
    
    response.then()
      .statusCode(200);
      
    IllusionEventParticipant[] participants = objectMapper.readValue(response.asString(), IllusionEventParticipant[].class);
    assertTrue(participants != null && participants.length == 1);
    
    IllusionEventParticipant participant = participants[0];
    participant.setRole(role);
  
    createRestClient()
      .body(participant)
      .put(String.format("/rest/illusion/events/{EVENTID}/participants/{ID}"), eventId, participant.getId())
      .then()
      .statusCode(204);
  }

  private void waitMailsReceived(final GreenMail greenMail, final int count) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        MimeMessage[] messages = greenMail.getReceivedMessages();
        if (messages != null) {
          return messages.length == count;
        }
        
        return false;
      }
    });
  }
  
  private IllusionEventParticipant createEventParticipant(Long eventId, Long userId, IllusionEventParticipantRole role) throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    
    IllusionEventParticipant participant = new IllusionEventParticipant(null, userId, role);
    
    Response response = createRestClient()
      .body(participant)
      .post(String.format("/rest/illusion/events/{EVENTID}/participants"), eventId);
    
    response.then()
      .statusCode(200);
      
    return objectMapper.readValue(response.asString(), IllusionEventParticipant.class);
  }
  
  private RequestSpecification createRestClient() {
    return given()
      .header("Authorization", String.format("Bearer %s", "admin-access-token"))
      .contentType("application/json");
  }
}
