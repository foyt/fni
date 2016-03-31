package fi.foyt.fni.test.rest;

import static org.hamcrest.Matchers.is;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "tags", before = { "basic-tags-setup.sql" }, after = { "basic-tags-teardown.sql" }),
  @DefineSqlSet (id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql"),
  @DefineSqlSet (id = "user-client", before = "rest-user-client-setup.sql", after = "rest-user-client-teardown.sql")
})
public class MaterialsTagsRestTestsIT extends AbstractRestTest {
  
  @Test
  @SqlSets ({"tags", "service-client"})
  public void testListTags() {
    givenJson()
      .get("/material/tags")
      .then()
      .statusCode(200)
      .body("id.size()", is(3))
      .body("id[0]", is(1) )
      .body("text[0]", is("test"));
    ;
  }
  
}
