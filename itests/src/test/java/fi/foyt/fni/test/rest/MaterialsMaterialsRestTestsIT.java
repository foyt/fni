package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import fi.foyt.fni.rest.material.model.Material;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet (id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql"),
  @DefineSqlSet (id = "tags", before = { "basic-tags-setup.sql" }, after = { "basic-tags-teardown.sql" })
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
  @SqlSets({"basic-users", "user-client", "basic-materials", "tags"})
  public void testUpdateMaterial() throws OAuthSystemException, OAuthProblemException {
    Material material = givenJson("access-token")
      .get("/material/materials/{ID}", 3)
      .as(Material.class);
    
    assertNotNull(material);
    assertEquals(material.getId(), (Long) 3l);
    assertEquals(material.getTitle(), "Document");
    assertEquals(material.getDescription(), "Document material for automated tests");
    assertEquals(material.getTags().size(), 0);
    
    material.setTitle("Modified title");
    material.setDescription("Modified description");
    material.setLicense("Modified license");
    material.setTags(Arrays.asList("test", "tag", "with space"));
    
    givenJson("access-token")
      .body(material)
      .put("/material/materials/{ID}", 3)
      .then()
      .statusCode(200)
      .body("id", is(3))
      .body("type", is("DOCUMENT"))
      .body("publicity", is("PRIVATE"))
      .body("urlName", is("document"))
      .body("path", is("2/document"))
      .body("title", is(material.getTitle()))
      .body("description", is(material.getDescription()))
      .body("license", is(material.getLicense()))
      .body("tags", is(material.getTags()))
      .body("languageId", is((Long) null))
      .body("modified", is(not((String) null)))
      .body("created", is(not((String) null)))
      .body("creatorId", is(2))
      .body("modifierId", is(2))
      .body("parentFolderId", is((Long) null));     
    
    givenJson("access-token")
      .get("/material/materials/{ID}", 3)
      .then()
      .statusCode(200)
      .body("id", is(3))
      .body("type", is("DOCUMENT"))
      .body("publicity", is("PRIVATE"))
      .body("urlName", is("document"))
      .body("path", is("2/document"))
      .body("title", is(material.getTitle()))
      .body("description", is(material.getDescription()))
      .body("license", is(material.getLicense()))
      .body("tags", is(material.getTags()))
      .body("languageId", is((Long) null))
      .body("modified", is(not((String) null)))
      .body("created", is(not((String) null)))
      .body("creatorId", is(2))
      .body("modifierId", is(2))
      .body("parentFolderId", is((Long) null));     
  }
  
  
}
