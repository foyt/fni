package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;

import fi.foyt.fni.rest.users.model.User;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql")
})
public class UsersRestTestsIT extends AbstractRestTest {
  
  @Test
  public void testCreateUserUnauthorized() {
    givenJson()
      .post("/users/users")
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets ("service-client")
  public void testCreateUserWithoutCredentials() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      User user = new User(null, "Without", "Credentials", "credentialess", null, Arrays.asList("credentialess@foyt.fi"));
      
      givenJson(createServiceToken())
        .queryParam("generateCredentials", "false")
        .queryParam("password", "pass")
        .body(user)
        .post("/users/users")
        .then()
        .statusCode(200);
      
      deleteUser(user.getEmails().get(0));
      
      assertEquals(1, greenMail.getReceivedMessages().length);
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ("service-client")
  public void testCreateUserWithCredentialsNoSend() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      User user = new User(null, "API", "Created", "apicreated", null, Arrays.asList("apicreated@foyt.fi"));
      
      givenJson(createServiceToken())
        .queryParam("generateCredentials", "true")
        .queryParam("sendCredentials", "false")
        .body(user)
        .post("/users/users")
        .then()
        .statusCode(200);
      
      deleteUser(user.getEmails().get(0));
      
      assertEquals(0, greenMail.getReceivedMessages().length);
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ("service-client")
  public void testCreateUserWithCredentials() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      User user = new User(null, "API", "Created", "apicreated", null, Arrays.asList("apicreated@foyt.fi"));
      
      givenJson(createServiceToken())
        .queryParam("generateCredentials", "true")
        .queryParam("sendCredentials", "true")
        .body(user)
        .post("/users/users")
        .then()
        .statusCode(200);
      
      assertEquals(1, greenMail.getReceivedMessages().length);

      String mailSubject = greenMail.getReceivedMessages()[0].getSubject();
      String mailContent = (String) greenMail.getReceivedMessages()[0].getContent();
      
      assertEquals("Welcome to Forge & Illusion", mailSubject);
      assertTrue(mailContent.contains("Welcome to"));
      Pattern pattern = Pattern.compile("(.*password is \")(.*)(\".*)", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(mailContent);
      assertTrue(mailContent, matcher.matches());
      String password = matcher.group(2); 
      assertNotNull(password);
      
      assertInternalAuthExists(user.getEmails().get(0), password);

      deleteUser(user.getEmails().get(0));
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ({"basic-users", "service-client"})
  public void testListUsersUnauthorized() {
    givenJson()
      .get("/users/users")
      .then()
      .statusCode(401);

  }
  
  @Test
  @SqlSets ({"service-client"})
  public void testListUsersNoContent() throws Exception {
    givenJson(createServiceToken())
      .queryParam("email", "noone@foyt.fi")
      .get("/users/users")
      .then()
      .statusCode(200)
      .body("id.size()", is(0));
  }
  
  @Test
  @SqlSets ({"basic-users", "service-client"})
  public void testFindUserByEmail() throws Exception {
    givenJson(createServiceToken())
      .queryParam("email", "user@foyt.fi")
      .get("/users/users")
      .then()
      .statusCode(200)
      .body("id.size()", is(1))
      .body("id[0]", is(2))
      .body("firstName[0]", is("Test"))
      .body("lastName[0]", is("User"))
      .body("nickname[0]", is((String) null))
      .body("locale[0]", is("en_US"))
      .body("emails[0].size()", is(1))
      .body("emails[0][0]", is("user@foyt.fi"));
  }
  
}
