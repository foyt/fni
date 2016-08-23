package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.jayway.restassured.response.Response;

import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.rest.forum.model.ForumPost;
import fi.foyt.fni.rest.illusion.model.IllusionEvent;
import fi.foyt.fni.rest.illusion.model.IllusionEventParticipant;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlParam;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql"),
  @DefineSqlSet(id = "events", before = "illusion-event-oai-setup.sql", after = "illusion-event-oai-teardown.sql"),
  @DefineSqlSet(id = "events-upcoming", before = "illusion-upcoming-events-setup.sql", after = "illusion-upcoming-events-teardown.sql"),
  @DefineSqlSet(id = "groups", before = "illusion-event-oai-groups-setup.sql", after = "illusion-event-oai-groups-teardown.sql"),
  @DefineSqlSet(id = "posts", before = "illusion-event-oai-posts-setup.sql", after = "illusion-event-oai-posts-teardown.sql"),
  @DefineSqlSet(id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet(id = "event", before = { "illusion-event-setup.sql" }, after = { "illusion-event-teardown.sql"}),  
  @DefineSqlSet(id = "event-unpublished", before = { "illusion-event-open-unpublished-setup.sql" }, after = { "illusion-event-open-unpublished-teardown.sql"}),
  @DefineSqlSet(id = "event-participant", before = {"illusion-event-open-participant-setup.sql" }, after = {"illusion-event-open-participant-teardown.sql"}),
  @DefineSqlSet(id = "event-organizer", before = {"illusion-event-open-organizer-setup.sql" }, after = {"illusion-event-open-organizer-teardown.sql"}),
  @DefineSqlSet(id = "event-forum", before = { "illusion-event-open-forum-setup.sql" }, after = {"illusion-event-open-forum-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-posts", before = { "illusion-event-open-forum-posts-setup.sql" }, after = {"illusion-event-open-forum-posts-teardown.sql"}),
  @DefineSqlSet(id = "event-character-sheet", before = { "illusion-event-open-character-sheet-setup.sql" }, after = {"illusion-event-open-character-sheet-teardown.sql"}),
  @DefineSqlSet(id = "event-forum-visible", 
    before = {"illusion-event-setting-setup.sql" }, 
    after = {"illusion-event-setting-teardown.sql"}, params = {
      @SqlParam (name = "id", value = "1"), 
      @SqlParam (name = "eventId", value = "1"),
      @SqlParam (name = "value", value = "{\"FORUM\":{\"visibility\":\"VISIBLE\"}}") 
    }
  ) 
})
public class IllusionEventsRestTestIT extends AbstractRestTest {

  @Test
  @SqlSets({"basic-users", "events"})
  public void testGroupListUnauthorized() {
    givenJson()
      .get("/illusion/events/{EVENTID}/groups", 1)
      .then()
      .statusCode(401);
  }

  @Test
  @SqlSets({"basic-users","service-client", "events", "groups"})
  public void testGroupLists() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/illusion/events/{EVENTID}/groups", 2)
      .then()
      .statusCode(200)
      .body("id.size()", is(1))
      .body("id[0]", is(1) )
      .body("eventId[0]", is(2) )
      .body("name[0]", is("Test Group in event #2"));
    
    givenJson(token)
      .get("/illusion/events/{EVENTID}/groups", 3)
      .then()
      .statusCode(200)
      .body("id.size()", is(2))
      .body("id[0]", is(2) )
      .body("eventId[0]", is(3) )
      .body("name[0]", is("Test Group #1 in event #3"))
      .body("id[1]", is(3) )
      .body("eventId[1]", is(3) )
      .body("name[1]", is("Test Group #2 in event #3"));
    
    givenJson(token)
      .get("/illusion/events/{EVENTID}/groups", 4)
      .then()
      .statusCode(200)
      .body("id.size()", is(0));
  }

  @Test
  public void testEventListEmpty() throws OAuthSystemException, OAuthProblemException {
    givenJson()
      .get("/illusion/events/")
      .then()
      .statusCode(200)
      .body("id.size()", is(0));
  }

  @Test
  @SqlSets({"basic-users", "events"})
  public void testEventList() throws OAuthSystemException, OAuthProblemException {
    givenJson()
      .get("/illusion/events/")
      .then()
      .statusCode(200)
      .body("id.size()", is(3))
      .body("id[0]", is(2) )
      .body("name[0]", is("Open"))
      .body("description[0]", is("Event for automatic testing (Open)"))
      .body("created[0]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
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
      .body("signUpStartDate[0]", is((ZonedDateTime) null))
      .body("signUpEndDate[0]", is((ZonedDateTime) null))
      .body("domain[0]", is((String) null))
      .body("start[0]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end[0]", is(toDateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
      .body("genreIds[0].size()", is(0))
      
      .body("id[1]", is(3) )
      .body("name[1]", is("Approve"))
      .body("description[1]", is("Event for automatic testing (Approve)"))
      .body("created[1]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
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
      .body("signUpStartDate[1]", is((ZonedDateTime) null))
      .body("signUpEndDate[1]", is((ZonedDateTime) null))
      .body("domain[1]", is((String) null))
      .body("start[1]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end[1]", is(toDateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
      .body("genreIds[1].size()", is(0))
      
      .body("id[2]", is(4) )
      .body("name[2]", is("Invite Only"))
      .body("description[2]", is("Event for automatic testing (Invite Only)"))
      .body("created[2]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
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
      .body("signUpStartDate[2]", is((ZonedDateTime) null))
      .body("signUpEndDate[2]", is((ZonedDateTime) null))
      .body("domain[2]", is((String) null))
      .body("start[2]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end[2]", is(toDateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
      .body("genreIds[2].size()", is(0));
  }
     
  @Test
  @SqlSets({"basic-users", "events-upcoming"})
  public void testEventListByDates() {
    ZonedDateTime startTime = ZonedDateTime.now();
    ZonedDateTime endTime = ZonedDateTime.now();
    endTime.plusDays(3);
    
    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    Response response = givenJson()
      .queryParam("minTime", formatter.format(startTime))
      .queryParam("maxTime", formatter.format(endTime))
      .get("/illusion/events/");
    
    response.then().statusCode(200);
    
    int count = response.body().jsonPath().getInt("id.size()");
    assertTrue(count == 1);
    
    for (int i = 0; i < count - 1; i++) {
      String startPath = String.format("start[%s]", i);
      String endPath = String.format("end[%s]", i);
      
      ZonedDateTime eventStart = parseZonedDateTime(response.body().jsonPath().getString(startPath));
      ZonedDateTime eventEnd = parseZonedDateTime(response.body().jsonPath().getString(endPath));
      
      assertTrue(String.format("eventStart (%s) should before filter range end (%s)", eventStart, endTime), eventStart.isBefore(endTime));
      assertTrue(String.format("eventEnd (%s) should before filter range start (%s)", eventEnd, startTime), eventEnd.isBefore(startTime));
    }
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
      .body("created", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
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
      .body("signUpStartDate", is((ZonedDateTime) null))
      .body("signUpEndDate", is((ZonedDateTime) null))
      .body("domain", is((String) null))
      .body("start", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("end", is(toDateTime(2010, 1, 2, 0, 0, 0, 0).toString()))
      .body("genreIds.size()", is(0));
  }
  
  @Test
  @SqlSets({"basic-users","service-client", "illusion-basic"})
  public void testEventCreate() throws Exception {
    IllusionEvent event = new IllusionEvent(null, Boolean.TRUE, "Test Event", "Event for testing", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, null, "Twilight zone", 16, Boolean.TRUE, null, 1l, null, null, null, toDateTime(2015, 6, 7, 0, 0, 0, 0), toDateTime(2015, 6, 7, 0, 0, 0, 0), new ArrayList<Long>());
    
    Response response = givenJson(createServiceToken())
      .body(event)
      .post("/illusion/events");
    
    response
      .then()
      .body("id", is(not((Long) null)))
      .body("published", is(event.getPublished()))
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
      .body("signUpStartDate", is((ZonedDateTime) null))
      .body("signUpEndDate", is((ZonedDateTime) null))
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
  public void testEventPublish() throws Exception {
    String token = createServiceToken();
    
    IllusionEvent event = new IllusionEvent(null, Boolean.FALSE, "To be published", "Event to be published", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, null, "Location", 16, Boolean.TRUE, null, 1l, toDateTime(2015, 6, 7, 8, 9, 10, 11), toDateTime(2015, 7, 8, 9, 10, 11, 12), 
        null, toDateTime(2015, 6, 7, 8, 9, 0, 0), toDateTime(2015, 1, 2, 3, 4, 0, 0), new ArrayList<Long>());
    
    Response createResponse = givenJson(token)
      .body(event)
      .post("/illusion/events");
    
    createResponse.then()
      .body("id", is(not((Long) null)))      
      .body("published", is(event.getPublished()))
      .statusCode(200);

    Long id = createResponse.body().jsonPath().getLong("id");
    String urlName = createResponse.body().jsonPath().getString("urlName");
    
    givenJson(token)
      .get("/illusion/events/{ID}", id).then()
      .statusCode(200)
      .body("published", is(event.getPublished()));
    
    event.setPublished(Boolean.TRUE);
    
    givenJson(token)
      .body(event)
      .put("/illusion/events/{ID}", id)
      .then()
      .statusCode(204);

    givenJson(token)
      .get("/illusion/events/{ID}", id).then()
      .statusCode(200)
      .body("published", is(event.getPublished()));

    deleteIllusionEventByUrl(urlName);
    deleteIllusionFolderByUser("servicetest@foyt.fi");
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic" })
  public void testEventUnpublish() throws Exception {
    String token = createServiceToken();
    
    IllusionEvent event = new IllusionEvent(null, Boolean.TRUE, "To be published", "Event to be published", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, null, "Location", 16, Boolean.TRUE, null, 1l, toDateTime(2015, 6, 7, 8, 9, 10, 11), toDateTime(2015, 7, 8, 9, 10, 11, 12), 
        null, toDateTime(2015, 6, 7, 8, 9, 0, 0), toDateTime(2015, 1, 2, 3, 4, 0, 0), new ArrayList<Long>());
    
    Response createResponse = givenJson(token)
      .body(event)
      .post("/illusion/events");
    
    createResponse.then()
      .body("id", is(not((Long) null)))      
      .body("published", is(event.getPublished()))
      .statusCode(200);

    Long id = createResponse.body().jsonPath().getLong("id");
    String urlName = createResponse.body().jsonPath().getString("urlName");
    
    givenJson(token)
      .get("/illusion/events/{ID}", id).then()
      .statusCode(200)
      .body("published", is(event.getPublished()));
    
    event.setPublished(Boolean.FALSE);
    
    givenJson(token)
      .body(event)
      .put("/illusion/events/{ID}", id)
      .then()
      .statusCode(204);

    givenJson(token)
      .get("/illusion/events/{ID}", id).then()
      .statusCode(200)
      .body("published", is(event.getPublished()));

    deleteIllusionEventByUrl(urlName);
    deleteIllusionFolderByUser("servicetest@foyt.fi");
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic" })
  public void testEventUpdate() throws Exception {
    String token = createServiceToken();
    
    IllusionEvent createEvent = new IllusionEvent(null, Boolean.TRUE, "To be modified", "Event to be modified", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, null, "Unmodified location", 16, Boolean.TRUE, null, 1l, toDateTime(2015, 6, 7, 8, 9, 10, 11), toDateTime(2015, 7, 8, 9, 10, 11, 12), 
        null, toDateTime(2015, 6, 7, 8, 9, 0, 0), toDateTime(2015, 1, 2, 3, 4, 0, 0), new ArrayList<Long>());
    
    Response createResponse = givenJson(token)
      .body(createEvent)
      .post("/illusion/events");
    
    createResponse.then()
      .body("id", is(not((Long) null)))      
      .body("published", is(createEvent.getPublished()))
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
    
    IllusionEvent updateEvent = new IllusionEvent(id, Boolean.TRUE, "Changed", "Changed description", null, null, null, IllusionEventJoinMode.APPROVE, 
        null, 5.30, "EUR", "Central Park", 18, Boolean.FALSE, "http://www.fake.com/image.png", 2l, 
        toDateTime(2020, 6, 7, 0, 0, 0, 0), toDateTime(2020, 7, 8, 0, 0, 0, 0), "www.customized.com", 
        toDateTime(2020, 9, 7, 0, 0, 0, 0), toDateTime(2020, 10, 8, 0, 0, 0, 0), Arrays.asList(2l));
    
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
      .body("published", is(updateEvent.getPublished()))
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
      .body("signUpStartDate", sameInstant(updateEvent.getSignUpStartDate().toInstant()))
      .body("signUpEndDate", sameInstant(updateEvent.getSignUpEndDate().toInstant()))
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
    IllusionEvent event = new IllusionEvent(null, Boolean.TRUE, "Test Event", "Event for testing", null, null, null, IllusionEventJoinMode.OPEN, 
        null, null, null, "Twilight zone", 16, Boolean.TRUE, null, 1l, toDateTime(2015, 6, 7, 8, 9, 10, 11), toDateTime(2015, 7, 8, 9, 10, 11, 12), 
        null, toDateTime(2015, 6, 7, 8, 9, 0, 0), toDateTime(2015, 1, 2, 3, 4, 0, 0), new ArrayList<Long>());
    
    Response response = givenJson(createServiceToken())
      .body(event)
      .post("/illusion/events");
    
    response
      .then()
      .body("id", is(not((Long) null)))
      .body("name", is(event.getName()))      
      .body("published", is(event.getPublished()))
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
  @SqlSets({"basic-users", "illusion-basic", "event", "event-unpublished", "event-participant" })
  public void testFindEventParticipantUnauthorized() {
    givenJson()
      .get("/illusion/events/1/participants/1")
      .then()
      .statusCode(401);
  }

  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event", "event-participant", "events"})
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
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event", "event-participant" })
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
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-unpublished" })
  public void testListParticipantsUnauthorized() throws Exception {
    givenJson("access-token")
      .get("/illusion/events/1/participants")
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event" })
  public void testListParticipantsEmpty() throws Exception {
    givenJson(createServiceToken())
      .get("/illusion/events/1/participants")
      .then()
      .statusCode(204);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event", "event-participant" })
  public void testListParticipants() throws Exception {
    givenJson(createServiceToken())
      .get("/illusion/events/1/participants")
      .then()
      .statusCode(200)
      .body("id.size", is(1))
      .body("id[0]", is(1))
      .body("role[0]", is("PARTICIPANT"))
      .body("userId[0]", is(2));
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event", "event-participant" })
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
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event", "event-participant" })
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
  @SqlSets({"basic-users", "service-client", "illusion-basic", "event", "event-participant" })
  public void testUpdateParticipant() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
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
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-posts"})
  public void testCreatePost() {
    ForumPost post = new ForumPost(null, null, "created post", null, null, null, null);
    
    givenJson("access-token")
      .body(post)
      .post("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(200)
      .body("topicId", is(20000))
      .body("content", is(post.getContent()))
      .body("modified", is(not((String) null)))
      .body("created", is(not((String) null)))
      .body("authorId", is(2))
      .body("views", is(0));
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum"})
  public void testListPostsUnauthorized() {
    givenJson()
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(401);
  }
  
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-forum"})
  public void testListPostsForbiddeb() {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible"})
  public void testListPostsEmpty() {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(204);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible", "event-forum-posts"})
  public void testListPosts() {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(200)
      .body("id.size()", is(2))
      .body("id[0]", is(20100))
      .body("topicId[0]", is(20000))
      .body("content[0]", is("message #1"))
      .body("modified[0]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("created[0]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("authorId[0]", is(2))
      .body("views[0]", is(0))
      .body("id[1]", is(20101))
      .body("topicId[1]", is(20000))
      .body("content[1]", is("message #2"))
      .body("modified[1]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("created[1]", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("authorId[1]", is(2))
      .body("views[1]", is(0));
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible", "event-forum-posts"})
  public void testFindPost() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", 1, 20100)
      .then()
      .statusCode(200)
      .body("id", is(20100))
      .body("topicId", is(20000))
      .body("content", is("message #1"))
      .body("modified", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("created", is(toDateTime(2010, 1, 1, 0, 0, 0, 0).toString()))
      .body("authorId", is(2))
      .body("views", is(0));
  }

  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-posts"})
  public void testFindPostUnauthorized() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", 1, 20100)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible", "event-forum-posts"})
  public void testFindPostNotFound() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", 1, 10100)
      .then()
      .statusCode(404);
    
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", -1, 20100)
      .then()
      .statusCode(404);
    
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", -1, -1)
      .then()
      .statusCode(404);
    
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", "abc", 20100)
      .then()
      .statusCode(404);
    
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", "abc", "abc")
      .then()
      .statusCode(404);

    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", "~", 20100)
      .then()
      .statusCode(404);

    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", "~", "~")
      .then()
      .statusCode(404);

    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", "%", 20100)
      .then()
      .statusCode(404);

    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts/{ID}", "%", "%")
      .then()
      .statusCode(404); 
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible", "event-forum-posts"})
  public void testUpdatePost() {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(200)
      .body("id.size()", is(2))
      .body("id[0]", is(20100))
      .body("content[0]", is("message #1"));
    
    ForumPost post = new ForumPost(20100l, null, "Updated", null, null, null, null);

    givenJson("access-token")
      .body(post)
      .put("/illusion/events/{EVENTID}/forumPosts/{ID}", 1, 20100)
      .then()
      .statusCode(204);

    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(200)
      .body("id.size()", is(2))
      .body("id[0]", is(20100))
      .body("content[0]", is("Updated"));
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "illusion-basic", "event", "event-participant", "event-forum", "event-forum-visible", "event-forum-posts"})
  public void testDeletePost() {
    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(200)
      .body("id.size()", is(2))
      .body("id[0]", is(20100))
      .body("id[1]", is(20101));
    
    givenJson("access-token")
      .delete("/illusion/events/{EVENTID}/forumPosts/{ID}", 1, 20100)
      .then()
      .statusCode(204);

    givenJson("access-token")
      .get("/illusion/events/{EVENTID}/forumPosts", 1)
      .then()
      .statusCode(200)
      .body("id.size()", is(1))
      .body("id[0]", is(20101));
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-character-sheet" })
  public void testCharacterSheetDataUnauthorized() throws Exception {
    givenJson()
      .get("/illusion/events/{EVENTID}/characterSheets/{ID}/data", 1, 1)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-character-sheet" })
  public void testCharacterSheetDataAccessDenied() throws Exception {
    givenJson("access-token")
      .queryParam("format", "XLS")
      .get("/illusion/events/{EVENTID}/characterSheets/{ID}/data", 1, 1)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "illusion-basic", "user-client", "event", "event-organizer", "event-character-sheet" })
  public void testCharacterSheetData() throws Exception {
    givenJson("admin-access-token")
      .queryParam("format", "XLS")
      .get("/illusion/events/{EVENTID}/characterSheets/{ID}/data", 1, 20060)
      .then()
      .statusCode(200);
  }

  private ZonedDateTime toZonedDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
    return ZonedDateTime.of(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0, ZoneId.systemDefault());
  }

  private OffsetDateTime toDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
    ZonedDateTime zonedDateTime = toZonedDateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
    return zonedDateTime.toOffsetDateTime();
  }

  private ZonedDateTime parseZonedDateTime(String text) {
    TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_DATE_TIME.parse(text);
    if (temporalAccessor == null) {
      return null;
    }
    
    return ZonedDateTime.from(temporalAccessor);
  }
  
}
