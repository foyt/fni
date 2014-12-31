package fi.foyt.fni.test.rest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
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
