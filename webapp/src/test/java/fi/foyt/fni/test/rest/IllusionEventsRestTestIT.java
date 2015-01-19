package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.DateTime;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.rest.illusion.model.IllusionEvent;
import fi.foyt.fni.rest.illusion.model.IllusionEventParticipant;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "events", before = "illusion-event-oai-setup.sql", after = "illusion-event-oai-teardown.sql"),
  @DefineSqlSet(id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet(id = "event-participant", before = { "illusion-event-open-setup.sql","illusion-event-open-participant-setup.sql" }, after = {"illusion-event-open-participant-teardown.sql","illusion-event-open-teardown.sql"})
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
  @SqlSets({"basic-users", "service-client", "events"})
  public void testEventFind() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .get("/illusion/events/{ID}", 2l)
      .then()
      .statusCode(200)
      .body("id", is(2) )
      .body("name", is("Open"))
      .body("description", is("Event for automatic testing (Open)"))
      .body("created", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("urlName", is("open"))
      .body("xmppRoom", is("open@bogustalk.net"))
      .body("joinMode", is("OPEN"))
      .body("signUpFee", is((Double) null))
      .body("signUpFeeCurrency", is((String) null))
      .body("location", is((String) null))
      .body("ageLimit", is((Integer) null))
      .body("beginnerFriendly", is((Boolean) null))
      .body("imageUrl", is((String) null))
      .body("typeId", is((Long) null))
      .body("signUpStartDate", is((DateTime) null))
      .body("signUpEndDate", is((DateTime) null))
      .body("domain", is((String) null))
      .body("start", is(new DateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end", is(new DateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
      .body("genreIds.size()", is(0));
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
    deleteIllusionFolderByUser("servicetest@foyt.fi");
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic" })
  public void testEventUpdate() throws Exception {
    String token = createServiceToken();
    
    IllusionEvent createEvent = new IllusionEvent(null, "To be modified", "Event to be modified", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, "Unmodified location", 16, Boolean.TRUE, null, 1l, new DateTime(2015, 6, 7, 8, 9, 10, 11), new DateTime(2015, 7, 8, 9, 10, 11, 12), 
        null, new DateTime(2015, 6, 7, 8, 9, 0, 0), new DateTime(2015, 1, 2, 3, 4, 0, 0), new ArrayList<Long>());
    
    Response createResponse = givenJson(token)
      .body(createEvent)
      .post("/illusion/events");
    
    createResponse.then()
      .body("id", is(not((Long) null)))
      .body("name", is(createEvent.getName()))
      .body("description", is(createEvent.getDescription()))
      .body("created", is(not((String) null)))
      .body("urlName", is(not((String) null)))
      .body("xmppRoom", is(not((String) null)))
      .body("joinMode", is(createEvent.getJoinMode().toString()))
      .body("signUpFee", is((Double) null))
      .body("signUpFeeCurrency", is((String) null))
      .body("location", is(createEvent.getLocation()))
      .body("ageLimit", is(createEvent.getAgeLimit()))
      .body("beginnerFriendly", is(createEvent.getBeginnerFriendly()))
      .body("imageUrl", is((String) null))
      .body("typeId", is(createEvent.getTypeId().intValue()))
      .body("signUpStartDate", is(createEvent.getSignUpStartDate().toString()))
      .body("signUpEndDate", is(createEvent.getSignUpEndDate().toString()))
      .body("domain", is((String) null))
      .body("start", is(createEvent.getStart().toString()))
      .body("end", is(createEvent.getEnd().toString()))
      .body("genreIds.size()", is(0))
      .statusCode(200);
    

    Long id = createResponse.body().jsonPath().getLong("id");
    
    IllusionEvent updateEvent = new IllusionEvent(id, "Changed", "Changed description", null, null, null, IllusionEventJoinMode.APPROVE, 
        5.30, "EUR", "Central Park", 18, Boolean.FALSE, "http://www.fake.com/image.png", 2l, 
        new DateTime(2020, 6, 7, 0, 0, 0, 0), new DateTime(2020, 7, 8, 0, 0, 0, 0), "www.customized.com", 
        new DateTime(2020, 9, 7, 0, 0, 0, 0), new DateTime(2020, 10, 8, 0, 0, 0, 0), Arrays.asList(2l));
    
    givenJson(token)
      .body(updateEvent)
      .put("/illusion/events/{ID}", id)
      .then()
      .statusCode(204);

    Response updatedResponse = givenJson(token)
      .get("/illusion/events/{ID}", id);
    
    updatedResponse.then()
      .statusCode(200)
      .body("id", is(id.intValue()) )
      .body("name", is(updateEvent.getName()))
      .body("description", is(updateEvent.getDescription()))
      .body("created", is(not((String) null)))
      .body("urlName", is(not((String) null)))
      .body("xmppRoom", is(not((String) null)))
      .body("joinMode", is(updateEvent.getJoinMode().toString()))
      .body("signUpFee", is(updateEvent.getSignUpFee().floatValue()))
      .body("signUpFeeCurrency", is(updateEvent.getSignUpFeeCurrency()))
      .body("location", is(updateEvent.getLocation()))
      .body("ageLimit", is(updateEvent.getAgeLimit()))
      .body("beginnerFriendly", is(updateEvent.getBeginnerFriendly()))
      .body("imageUrl", is(updateEvent.getImageUrl()))
      .body("typeId", is(updateEvent.getTypeId().intValue()))
      .body("signUpStartDate", sameInstant(updateEvent.getSignUpStartDate()))
      .body("signUpEndDate", sameInstant(updateEvent.getSignUpEndDate()))
      .body("domain", is(updateEvent.getDomain()))
      .body("start", is(updateEvent.getStart().toString()))
      .body("end", is(updateEvent.getEnd().toString()))
      .body("genreIds.size()", is(1))
      .statusCode(200);

    String urlName = updatedResponse.body().jsonPath().getString("urlName");
    
    deleteIllusionEventByUrl(urlName);
    deleteIllusionFolderByUser("servicetest@foyt.fi");
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic"})
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
    deleteIllusionFolderByUser("servicetest@foyt.fi");
  }

  @Test
  public void testFindEventParticipantUnauthorized() {
    givenJson()
      .get("/illusion/events/1/participants/1")
      .then()
      .statusCode(401);
  }

  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event-participant", "events"})
  public void testFindEventParticipantNotFound() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/illusion/events/123/participants/1")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/-1/participants/1")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/abc/participants/1")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/~/participants/1")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/%/participants/1")
      .then()
      .statusCode(404);    
    
    givenJson(token)
      .get("/illusion/events/1/participants/123")
      .then()
      .statusCode(404);
  
    givenJson(token)
      .get("/illusion/events/1/participants/-1")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/1/participants/abc")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/1/participants/~")
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/illusion/events/1/participants/%")
      .then()
      .statusCode(404);    
    
    givenJson(token)
      .get("/illusion/events/2/participants/1")
      .then()
      .statusCode(404);    
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event-participant" })
  public void testFindParticipant() throws Exception {
    givenJson(createServiceToken())
      .get("/illusion/events/1/participants/1")
      .then()
      .statusCode(200)
      .body("id", is(1))
      .body("role", is("PARTICIPANT"))
      .body("userId", is(2));
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event-participant" })
  public void testCreateParticipant() throws Exception {
    String token = createServiceToken();
    
    IllusionEventParticipant participant = new IllusionEventParticipant(null, 1l, IllusionEventParticipantRole.PENDING_APPROVAL);
    
    Response response = givenJson(token)
      .body(participant)
      .post("/illusion/events/{EVENTID}/participants", 1l);

    response.then()
      .statusCode(200)
      .body("id", not(is((Long) null)))
      .body("role", is("PENDING_APPROVAL"))
      .body("userId", is(1));
    
    long id = response.body().jsonPath().getLong("id");
    
    givenJson(token)
      .delete("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(204);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event-participant" })
  public void testDeleteParticipant() throws Exception {
    String token = createServiceToken();
    
    IllusionEventParticipant participant = new IllusionEventParticipant(null, 1l, IllusionEventParticipantRole.PENDING_APPROVAL);
    
    Response response = givenJson(token)
      .body(participant)
      .post("/illusion/events/{EVENTID}/participants", 1l);    
    response.then()
      .statusCode(200);
    
    long id = response.body().jsonPath().getLong("id");
    givenJson(createServiceToken())
      .get("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(200);
    
    givenJson(token)
      .delete("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(204);
    
    givenJson(createServiceToken())
      .get("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(404);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event-participant" })
  public void testUpdateParticipant() throws Exception {
    String token = createServiceToken();
    
    IllusionEventParticipant createParticipant = new IllusionEventParticipant(null, 1l, IllusionEventParticipantRole.PENDING_APPROVAL);
    
    Response response = givenJson(token)
      .body(createParticipant)
      .post("/illusion/events/{EVENTID}/participants", 1l);    
    response.then()
      .statusCode(200);
    
    Long id = response.body().jsonPath().getLong("id");
    givenJson(createServiceToken())
      .get("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(200)
      .body("id", not(is((Long) null)))
      .body("role", is("PENDING_APPROVAL"))
      .body("userId", is(1));
    
    IllusionEventParticipant updateParticipant = new IllusionEventParticipant(id, createParticipant.getUserId(), IllusionEventParticipantRole.ORGANIZER);
    
    givenJson(token)
      .body(updateParticipant)
      .put("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(204);
    
    givenJson(createServiceToken())
      .get("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(200)
      .body("id", is(id.intValue()))
      .body("role", is("ORGANIZER"))
      .body("userId", is(1));
    
    givenJson(token)
      .delete("/illusion/events/{EVENTID}/participants/{ID}", 1l, id)
      .then()
      .statusCode(204);
  }
}
