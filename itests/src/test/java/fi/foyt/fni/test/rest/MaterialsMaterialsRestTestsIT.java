package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Date;
import java.util.List;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet(id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql")
})
public class MaterialsMaterialsRestTestsIT extends AbstractRestTest {
  
  @Test
  @SqlSets({"basic-users", "basic-materials"})
  public void testFindMaterialUnauthorized() {
    givenJson()
      .get("/material/materials/{ID}", 3)
      .then()
      .statusCode(401);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "basic-materials"})
  public void testFindMaterialNotFound() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/material/materials/{ID}", 666)
      .then()
      .statusCode(404);
    
    givenJson(token)
      .get("/material/materials/{ID}", "ABC")
      .then()
      .statusCode(404);
  }
  
  @Test
  @SqlSets({"basic-users", "service-client", "basic-materials"})
  public void testFindMaterialForbiddenService() throws OAuthSystemException, OAuthProblemException {
    String token = createServiceToken();
    
    givenJson(token)
      .get("/material/materials/{ID}", 3)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindMaterialForbiddenUser() throws OAuthSystemException, OAuthProblemException {
    givenJson("guest-access-token")
      .get("/material/materials/{ID}", 3)
      .then()
      .statusCode(403);
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindMaterial() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/documents/{ID}", 3)
      .then()
      .statusCode(200)
      .body("id", is(3))
      .body("type", is("DOCUMENT"))
      .body("publicity", is("PRIVATE"))
      .body("urlName", is("document"))
      .body("path", is("2/document"))
      .body("title", is("Document"))
      .body("description", is("Document material for automated tests"))
      .body("license", is("http://creativecommons.org/licenses/by-sa/4.0/"))
      .body("tags.size()", is(0))
      .body("languageId", is((Long) null))
      .body("modified", is(not((String) null)))
      .body("created", is(not((String) null)))
      .body("creatorId", is(2))
      .body("modifierId", is(2))
      .body("parentFolderId", is((Long) null));     
  }
  
}
