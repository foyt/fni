package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.DateTime;
import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "events", before = "illusion-event-oai-setup.sql", after = "illusion-event-oai-teardown.sql") 
})
public class IllusionEventsRestTestIT extends AbstractRestTest {

  @Test
  public void testEventListUnauthorized() {
    givenJson()
      .get("/illusion/events/")
      .then()
      .statusCode(401);
  }

  @Test
  @SqlSets({"basic-users","service-client"})
  public void testEventListEmpty() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .get("/illusion/events/")
      .then()
      .statusCode(204);
  }

  @Test
  @SqlSets({"basic-users","service-client", "events"})
  public void testEventList() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .get("/illusion/events/")
      .then()
      .statusCode(200)
      .body("id.size()", is(3))
      .body("id[0]", is(2) )
      .body("name[0]", is("Open"))
      .body("description[0]", is("Event for automatic testing (Open)"))
      .body("created[0]", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("urlName[0]", is("open"))
      .body("xmppRoom[0]", is("open@bogustalk.net"))
      .body("joinMode[0]", is("OPEN"))
      .body("signUpFee[0]", is((Double) null))
      .body("signUpFeeCurrency[0]", is((String) null))
      .body("location[0]", is((String) null))
      .body("ageLimit[0]", is((Integer) null))
      .body("beginnerFriendly[0]", is((Boolean) null))
      .body("imageUrl[0]", is((String) null))
      .body("typeId[0]", is((Long) null))
      .body("signUpStartDate[0]", is((DateTime) null))
      .body("signUpEndDate[0]", is((DateTime) null))
      .body("domain[0]", is((String) null))
      .body("start[0]", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end[0]", is((DateTime) null))
      .body("genreIds[0].size()", is(0))
      
      .body("id[1]", is(3) )
      .body("name[1]", is("Approve"))
      .body("description[1]", is("Event for automatic testing (Approve)"))
      .body("created[1]", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("urlName[1]", is("approve"))
      .body("xmppRoom[1]", is("approve@bogustalk.net"))
      .body("joinMode[1]", is("APPROVE"))
      .body("signUpFee[1]", is((Double) null))
      .body("signUpFeeCurrency[1]", is((String) null))
      .body("location[1]", is((String) null))
      .body("ageLimit[1]", is((Integer) null))
      .body("beginnerFriendly[1]", is((Boolean) null))
      .body("imageUrl[1]", is((String) null))
      .body("typeId[1]", is((Long) null))
      .body("signUpStartDate[1]", is((DateTime) null))
      .body("signUpEndDate[1]", is((DateTime) null))
      .body("domain[1]", is((String) null))
      .body("start[1]", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end[1]", is((DateTime) null))
      .body("genreIds[1].size()", is(0))
      
      .body("id[2]", is(4) )
      .body("name[2]", is("Invite Only"))
      .body("description[2]", is("Event for automatic testing (Invite Only)"))
      .body("created[2]", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("urlName[2]", is("invite"))
      .body("xmppRoom[2]", is("invite@bogustalk.net"))
      .body("joinMode[2]", is("INVITE_ONLY"))
      .body("signUpFee[2]", is((Double) null))
      .body("signUpFeeCurrency[2]", is((String) null))
      .body("location[2]", is((String) null))
      .body("ageLimit[2]", is((Integer) null))
      .body("beginnerFriendly[2]", is((Boolean) null))
      .body("imageUrl[2]", is((String) null))
      .body("typeId[2]", is((Long) null))
      .body("signUpStartDate[2]", is((DateTime) null))
      .body("signUpEndDate[2]", is((DateTime) null))
      .body("domain[2]", is((String) null))
      .body("start[2]", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end[2]", is((DateTime) null))
      .body("genreIds[2].size()", is(0));
  }

  @Test
  public void testEventGetUnauthorized() {
    givenJson()
      .get("/illusion/events/123")
      .then()
      .statusCode(401);
  }

  @Test
  @SqlSets({"basic-users","service-client"})
  public void testEventNotFound() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .get("/illusion/events/123")
      .then()
      .statusCode(404);
    
    givenJson(createServiceToken())
      .get("/illusion/events/-1")
      .then()
      .statusCode(404);
    
    givenJson(createServiceToken())
      .get("/illusion/events/abc")
      .then()
      .statusCode(404);
    
    givenJson(createServiceToken())
      .get("/illusion/events/~")
      .then()
      .statusCode(404);
    
    givenJson(createServiceToken())
      .get("/illusion/events/%")
      .then()
      .statusCode(404);    
  }
  
  @Test
  @SqlSets({"basic-users","service-client", "events"})
  public void testEventFind() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .get("/illusion/events/2")
      .then()
      .statusCode(200);
  }
  
}
