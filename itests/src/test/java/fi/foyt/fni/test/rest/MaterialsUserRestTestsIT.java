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
      .body("id[0]", is(3))
      .body("userId[0]", is(3))
      .body("role[0]", is("MAY_VIEW"))
      .body("id[1]", is(4))
      .body("userId[1]", is(4))
      .body("role[1]", is("MAY_EDIT"));
  }

  @Test
  @SqlSets({"basic-users", "user-client", "basic-materials"})
  public void testFindMaterialUser() throws OAuthSystemException, OAuthProblemException {
    givenJson("access-token")
      .get("/material/materials/{ID}/users/{ID}", 3, 3)
      .then()
      .statusCode(200)
      .body("id", is(3))
      .body("userId", is(3))
      .body("role", is("MAY_VIEW"));
  }
}
