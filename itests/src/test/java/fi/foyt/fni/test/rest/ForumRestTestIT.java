package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import fi.foyt.fni.rest.forum.model.ForumTopicWatcher;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ 
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql"),
  @DefineSqlSet(id = "forum-basic", before = "basic-forum-setup.sql", after = "basic-forum-teardown.sql"),
  @DefineSqlSet(id = "forum-watchers", before = "forum-watchers-setup.sql", after = "forum-watchers-teardown.sql")
})
public class ForumRestTestIT extends AbstractRestTest {

  @Test
  @SqlSets({"basic-users", "forum-basic"})
  public void testCreateTopicWatcherUnauthorized() {
    ForumTopicWatcher payload = new ForumTopicWatcher(null, 1l);
    
    givenJson()
      .body(payload)
      .post("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "service-client"})
  public void testCreateTopicWatcherForbiddenService() throws OAuthSystemException, OAuthProblemException {
    ForumTopicWatcher payload = new ForumTopicWatcher(null, 1l);
    
    givenJson(createServiceToken())
      .body(payload)
      .post("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "user-client"})
  public void testCreateTopicWatcherForbidden() {
    ForumTopicWatcher payload = new ForumTopicWatcher(null, 4l);
    
    givenJson("access-token")
      .body(payload)
      .post("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "user-client"})
  public void testCreateTopicWatcher() {
    ForumTopicWatcher payload = new ForumTopicWatcher(null, 2l);

    givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(204);
    
    givenJson("access-token")
      .body(payload)
      .post("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(200);

    Response response = givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6);
    
    response.then()
      .statusCode(200)
      .body("id.size()", is(1))
      .body("id[0]", not(is((Long) null)) )
      .body("userId[0]", is(2));
    
    int id = response.jsonPath().getInt("id[0]");

    givenJson("access-token")
      .delete("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/{ID}", 4, 6, id)
      .then()
      .statusCode(204);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic"})
  public void testListTopicWatchersUnauthorized() {
    givenJson()
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "service-client"})
  public void testListTopicWatchersForbiddenService() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "user-client"})
  public void testListTopicWatchersForbidden() {
    givenJson("access-token")
      .queryParam("userId", "4")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "user-client"})
  public void testListTopicWatchers() {
    ForumTopicWatcher payload = new ForumTopicWatcher(null, 2l);
  
    givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(204);
    
    givenJson("access-token")
      .body(payload)
      .post("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(200);
  
    Response response = givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6);
    
    response.then()
      .statusCode(200)
      .body("id.size()", is(1))
      .body("id[0]", not(is((Long) null)) )
      .body("userId[0]", is(2));
    
    int id = response.jsonPath().getInt("id[0]");
  
    givenJson("access-token")
      .delete("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/{ID}", 4, 6, id)
      .then()
      .statusCode(204);
    
    givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 4, 6)
      .then()
      .statusCode(204);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "forum-watchers"})
  public void testDeleteTopicWatchersUnauthorized() {
    givenJson()
      .delete("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/{ID}", 3, 1, 1)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "service-client", "forum-watchers"})
  public void testDeleteTopicWatchersForbiddenService() throws OAuthSystemException, OAuthProblemException {
    givenJson(createServiceToken())
      .delete("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/{ID}", 3, 1, 1)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "user-client", "forum-watchers"})
  public void testDeleteTopicWatchersForbidden() {
    givenJson("access-token")
      .delete("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/{ID}", 3, 1, 2)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "forum-basic", "user-client", "forum-watchers"})
  public void testDeleteTopicWatcher() {
    givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 3, 1)
      .then()
      .statusCode(200)
      .body("id.size()", is(1));
    
    givenJson("access-token")
      .delete("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/{ID}", 3, 1, 1)
      .then()
      .statusCode(204);
    
    givenJson("access-token")
      .queryParam("userId", "2")
      .get("/forum/forums/{FORUMID}/topics/{TOPICID}/watchers/", 3, 1)
      .then()
      .statusCode(204);
  }

}
