package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql")
})
public class MaterialsImagesRestTestIT extends AbstractRestTest {
  
  @Test
  @SqlSets({"basic-users", "basic-materials"})
  public void testFindImageUnauthorized() {
    givenJson()
      .get("/material/images/{ID}", 6)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "basic-materials"})
  public void testFindImageNotFound() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/material/images/{ID}", 3)
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/material/images/{ID}", 666)
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/material/images/{ID}", "ABC")
      .then()
      .statusCode(404);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "basic-materials"})
  public void testFindImageForbiddenService() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/material/images/{ID}", 6)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindImageForbiddenUser() throws OAuthSystemException, OAuthProblemException {
    givenJson("guest-access-token")
      .get("/material/images/{ID}", 6)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindImage() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/images/{ID}", 6)
      .then()
      .statusCode(200)
      .body("id", is(6))
      .body("type", is("IMAGE"))
      .body("publicity", is("PRIVATE"))
      .body("urlName", is("image"))
      .body("title", is("Image"))
      .body("languageId", is((Long) null))
//      .body("modified", is(getDate(2010, 1, 1).toString()))
//      .body("created", is(getDate(2010, 1, 1).toString()))
      .body("creatorId", is(2))
      .body("modifierId", is(2))
      .body("parentFolderId", is((Long) null));  
  }
  
}
