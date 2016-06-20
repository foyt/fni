package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import com.jayway.restassured.response.Response;

import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.rest.material.model.MaterialShareGroup;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "user-groups", before = { "basic-user-groups-setup.sql" }, after = { "basic-user-groups-teardown.sql" }),
  @DefineSqlSet (id = "group-shares", before = { "basic-material-group-shares-setup.sql" }, after = { "basic-material-group-shares-teardown.sql" }),
  @DefineSqlSet (id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql")
})
public class MaterialsGroupsRestTestsIT extends AbstractRestTest {

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials", "user-groups", "group-shares"})
  public void testListMaterialGroups() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/materials/{ID}/shareGroups", 19)
      .then()
      .statusCode(200)
      .body("id.size()", is(1))
      .body("userGroupId[0]", is(2001))
      .body("role[0]", is("MAY_VIEW"));
  }
  
  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials", "user-groups", "group-shares"})
  public void testFindMaterialGroup() throws OAuthSystemException, OAuthProblemException {
    MaterialShareGroup materialShareGroup = givenJson("access-token")
      .get("/material/materials/{ID}/shareGroups", 19)
      .as(MaterialShareGroup[].class)[0];
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/shareGroups/{ID}", 19, materialShareGroup.getId())
      .then()
      .statusCode(200)
      .body("id", is(materialShareGroup.getId().intValue()))
      .body("userGroupId", is(materialShareGroup.getUserGroupId().intValue()))
      .body("role", is(materialShareGroup.getRole().toString()));
  }

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials", "user-groups", "group-shares"})
  public void testUpdateMaterialGroup() throws OAuthSystemException, OAuthProblemException {
    Response getResponse = givenJson("access-token")
        .get("/material/materials/{ID}/shareGroups", 19);
    
    MaterialShareGroup materialShareGroup = getResponse
        .as(MaterialShareGroup[].class)[0];
    
    getResponse
      .then()
      .statusCode(200)
      .body("userGroupId[0]", is(2001))
      .body("role[0]", is("MAY_VIEW"));
    
    materialShareGroup.setRole(MaterialRole.MAY_EDIT);
    
    givenJson("access-token")
      .body(materialShareGroup)
      .put("/material/materials/{MATERIALID}/shareGroups/{ID}", 19, materialShareGroup.getId())
      .then()
      .statusCode(200)
      .body("id", is(materialShareGroup.getId().intValue()))
      .body("userGroupId", is(materialShareGroup.getUserGroupId().intValue()))
      .body("role", is(materialShareGroup.getRole().toString()));
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/shareGroups/{ID}", 19, materialShareGroup.getId())
      .then()
      .statusCode(200)
      .body("id", is(materialShareGroup.getId().intValue()))
      .body("userGroupId", is(materialShareGroup.getUserGroupId().intValue()))
      .body("role", is(materialShareGroup.getRole().toString()));
  }

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials", "user-groups", "group-shares"})
  public void testCreateAndDeleteMaterialGroup() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/materials/{ID}/shareGroups", 19)
      .then()
      .statusCode(200)
      .body("id.size()", is(1));
    
    MaterialShareGroup materialShareGroup = new MaterialShareGroup(null, 2002l, MaterialRole.MAY_EDIT);
    
    Response postResponse = givenJson("access-token")
      .body(materialShareGroup)
      .post("/material/materials/{ID}/shareGroups", 19);
    
    MaterialShareGroup createdShareGroup = postResponse.as(MaterialShareGroup.class);
    
    postResponse
      .then()
      .statusCode(200)
      .body("id", is(createdShareGroup.getId().intValue()))
      .body("userGroupId", is(createdShareGroup.getUserGroupId().intValue()))
      .body("role", is(createdShareGroup.getRole().toString()));
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/shareGroups", 19)
      .then()
      .statusCode(200)
      .body("id.size()", is(2));
      
    givenJson("access-token")
      .delete("/material/materials/{MATERIALID}/shareGroups/{ID}", 19, createdShareGroup.getId())
      .then()
      .statusCode(204);
    
    givenJson("access-token")
      .get("/material/materials/{MATERIALID}/shareGroups", 19)
      .then()
      .statusCode(200)
      .body("id.size()", is(1));
  }

}
