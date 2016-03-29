package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.rest.material.model.MaterialUser;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql"),
})
public class MaterialsUserRestTestsIT extends AbstractRestTest {

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testListMaterialUsers() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/materials/{ID}/users", 3)
      .then()
      .statusCode(200)
      .body("id.size()", is(2))
      .body("userId[0]", is(3))
      .body("role[0]", is("MAY_VIEW"))
      .body("userId[1]", is(4))
      .body("role[1]", is("MAY_EDIT"));
  }

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindMaterialUser() throws OAuthSystemException, OAuthProblemException {
    MaterialUser materialUser = givenJson("access-token")
      .get("/material/materials/{ID}/users", 3)
      .as(MaterialUser[].class)[0];
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/users/{ID}", 3, materialUser.getId())
      .then()
      .statusCode(200)
      .body("id", is(materialUser.getId().intValue()))
      .body("userId", is(materialUser.getUserId().intValue()))
      .body("role", is(materialUser.getRole().toString()));
  }

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testUpdateMaterialUser() throws OAuthSystemException, OAuthProblemException {
    Response getResponse = givenJson("access-token")
        .get("/material/materials/{ID}/users", 3);
    
    MaterialUser materialUser = getResponse
        .as(MaterialUser[].class)[0];
    
    getResponse
      .then()
      .statusCode(200)
      .body("userId[0]", is(3))
      .body("role[0]", is("MAY_VIEW"));
    
    materialUser.setRole(MaterialRole.MAY_EDIT);
    
    givenJson("access-token")
      .body(materialUser)
      .put("/material/materials/{MATERIALID}/users/{ID}", 3, materialUser.getId())
      .then()
      .statusCode(200)
      .body("id", is(materialUser.getId().intValue()))
      .body("userId", is(materialUser.getUserId().intValue()))
      .body("role", is(materialUser.getRole().toString()));
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/users/{ID}", 3, materialUser.getId())
      .then()
      .statusCode(200)
      .body("id", is(materialUser.getId().intValue()))
      .body("userId", is(materialUser.getUserId().intValue()))
      .body("role", is(materialUser.getRole().toString()));
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testCreateAndDeleteMaterialUser() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/materials/{ID}/users", 3)
      .then()
      .statusCode(200)
      .body("id.size()", is(2));
    
    MaterialUser materialUser = new MaterialUser(null, 6l, MaterialRole.MAY_EDIT);
    
    Response postResponse = givenJson("access-token")
      .body(materialUser)
      .post("/material/materials/{ID}/users", 3);
    
    MaterialUser createdUser = postResponse.as(MaterialUser.class);
    
    postResponse
      .then()
      .statusCode(200)
      .body("id", is(createdUser.getId().intValue()))
      .body("userId", is(createdUser.getUserId().intValue()))
      .body("role", is(createdUser.getRole().toString()));
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/users", 3)
      .then()
      .statusCode(200)
      .body("id.size()", is(3));
      
    givenJson("access-token")
      .delete("/material/materials/{MATERIALID}/users/{ID}", 3, createdUser.getId())
      .then()
      .statusCode(204);
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/users", 3)
      .then()
      .statusCode(200)
      .body("id.size()", is(2));
  }
}
