package fi.foyt.fni.test.ui.base.forgepublic;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet(id = "users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "public-materials", before = { "basic-materials-setup.sql", "basic-materials-public-setup.sql" }, after = "basic-materials-teardown.sql" ),
  
  
})
public class ForgePublicIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    testTitle("/forge/public", "Forge Public");
  }

  @Test
  @SqlSets ({"users", "public-materials"})
  public void testMostRecentAnonymous() {
    navigate("/forge/public");
    waitAndAssertSelectorText(".latest h3", "Latest", true, true);
    assertSelectorCount(".latest .material", 5);
    assertSelectorText(".latest .material:nth-child(1) h4 a", "Google Doc", true, true);
    assertSelectorText(".latest .material:nth-child(1) .modified", "MODIFIED: FRIDAY, JANUARY 22, 2010", true, true);
    assertSelectorText(".latest .material:nth-child(1) .creator-tag", "Test User", true, true);
  }
  
}
