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
public class MaterialsDocumentsRestTestIT extends AbstractRestTest {
  
  @Test
  @SqlSets({"basic-users", "basic-materials"})
  public void testFindDocumentUnauthorized() {
    givenJson()
      .get("/material/documents/{ID}", 3)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "basic-materials"})
  public void testFindDocumentNotFound() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/material/documents/{ID}", 6)
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/material/documents/{ID}", 666)
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/material/documents/{ID}", "ABC")
      .then()
      .statusCode(404);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "basic-materials"})
  public void testFindDocumentForbiddenService() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/material/documents/{ID}", 3)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindDocumentForbiddenUser() throws OAuthSystemException, OAuthProblemException {
    givenJson("guest-access-token")
      .get("/material/documents/{ID}", 3)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindDocument() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/documents/{ID}", 3)
      .then()
      .statusCode(200)
      .body("id", is(3))
      .body("type", is("DOCUMENT"))
      .body("publicity", is("PRIVATE"))
      .body("urlName", is("document"))
      .body("title", is("Document"))
      .body("languageId", is((Long) null))
//      .body("modified", is(getDate(2010, 1, 1).toString()))
//      .body("created", is(getDate(2010, 1, 1).toString()))
      .body("creatorId", is(2))
      .body("modifierId", is(2))
      .body("parentFolderId", is((Long) null));  
  }
  
}
