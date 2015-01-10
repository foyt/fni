package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.DateTime;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.rest.illusion.model.IllusionEvent;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "events", before = "illusion-event-oai-setup.sql", after = "illusion-event-oai-teardown.sql"),
  @DefineSqlSet(id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql")
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
      .body("end[0]", is(new DateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
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
      .body("end[1]", is(new DateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
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
      .body("end[2]", is(new DateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
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
  
  @Test
  @SqlSets({"basic-users","service-client", "illusion-basic"})
  public void testEventCreate() throws Exception {
    IllusionEvent event = new IllusionEvent(null, "Test Event", "Event for testing", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, "Twilight zone", 16, Boolean.TRUE, null, 1l, null, null, null, new DateTime(2015, 6, 7, 0, 0, 0, 0), new DateTime(2015, 6, 7, 0, 0, 0, 0), new ArrayList<Long>());
    
    Response response = givenJson(createServiceToken())
      .body(event)
      .post("/illusion/events");
    
    response
      .then()
      .body("id", is(not((Long) null)))
      .body("name", is(event.getName()))
      .body("description", is(event.getDescription()))
      .body("created", is(not((String) null)))
      .body("urlName", is(not((String) null)))
      .body("xmppRoom", is(not((String) null)))
      .body("joinMode", is(event.getJoinMode().toString()))
      .body("signUpFee", is((Double) null))
      .body("signUpFeeCurrency", is((String) null))
      .body("location", is(event.getLocation()))
      .body("ageLimit", is(event.getAgeLimit()))
      .body("beginnerFriendly", is(event.getBeginnerFriendly()))
      .body("imageUrl", is((String) null))
      .body("typeId", is(event.getTypeId().intValue()))
      .body("signUpStartDate", is((DateTime) null))
      .body("signUpEndDate", is((DateTime) null))
      .body("domain", is((String) null))
      .body("start", is(event.getStart().toString()))
      .body("end", is(event.getEnd().toString()))
      .body("genreIds.size()", is(0))
      .statusCode(200);
    
    String urlName = response.body().jsonPath().getString("urlName");
    
    deleteIllusionEventByUrl(urlName);
    deleteIllusionFolderByUser("admin@foyt.fi");
  }
  
  @Test
  @SqlSets({"basic-users","service-client", "illusion-basic"})
  public void testEventCreateWithDatesAndTimes() throws Exception {
    IllusionEvent event = new IllusionEvent(null, "Test Event", "Event for testing", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, "Twilight zone", 16, Boolean.TRUE, null, 1l, new DateTime(2015, 6, 7, 8, 9, 10, 11), new DateTime(2015, 7, 8, 9, 10, 11, 12), 
        null, new DateTime(2015, 6, 7, 8, 9, 0, 0), new DateTime(2015, 1, 2, 3, 4, 0, 0), new ArrayList<Long>());
    
    Response response = givenJson(createServiceToken())
      .body(event)
      .post("/illusion/events");
    
    response
      .then()
      .body("id", is(not((Long) null)))
      .body("name", is(event.getName()))
      .body("description", is(event.getDescription()))
      .body("created", is(not((String) null)))
      .body("urlName", is(not((String) null)))
      .body("xmppRoom", is(not((String) null)))
      .body("joinMode", is(event.getJoinMode().toString()))
      .body("signUpFee", is((Double) null))
      .body("signUpFeeCurrency", is((String) null))
      .body("location", is(event.getLocation()))
      .body("ageLimit", is(event.getAgeLimit()))
      .body("beginnerFriendly", is(event.getBeginnerFriendly()))
      .body("imageUrl", is((String) null))
      .body("typeId", is(event.getTypeId().intValue()))
      .body("signUpStartDate", is(event.getSignUpStartDate().toString()))
      .body("signUpEndDate", is(event.getSignUpEndDate().toString()))
      .body("domain", is((String) null))
      .body("start", is(event.getStart().toString()))
      .body("end", is(event.getEnd().toString()))
      .body("genreIds.size()", is(0))
      .statusCode(200);
    
    String urlName = response.body().jsonPath().getString("urlName");
    
    deleteIllusionEventByUrl(urlName);
    deleteIllusionFolderByUser("admin@foyt.fi");
  }
}
