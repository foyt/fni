package fi.foyt.fni.test.ui.base.forgepublic;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet(id = "users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "public-materials", before = { "basic-materials-setup.sql", "basic-materials-public-setup.sql", "basic-materials-views-setup.sql" }, after = { "basic-materials-views-teardown.sql", "basic-materials-teardown.sql" } ),
  @DefineSqlSet(id = "public-material-tags", before = { "basic-tags-setup.sql", "basic-materials-tags-setup.sql", "basic-materials-views-setup.sql" }, after = { "basic-materials-tags-teardown.sql", "basic-tags-teardown.sql" } ),
})
public class ForgePublicIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    testTitle("/forge/public", "Forge Public");
  }
  
  @Test
  @SqlSets ({"users", "public-materials"})
  public void testRandomMaterialAnonymous() {
    navigate("/forge/public");
    waitAndAssertSelectorText(".random h3", "Random material", true, true);
    assertSelectorCount(".random .material", 1);
    assertSelectorPresent(".random .material h4 a");
    assertSelectorPresent(".random .material .modified");
    assertSelectorPresent(".random .material .creator-tag");
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testMostPopularAnonymous() {
    navigate("/forge/public");
    waitAndAssertSelectorText(".most-popular h3", "Most Popular", true, true);
    assertSelectorCount(".most-popular .material", 5);
    assertSelectorText(".most-popular .material:nth-child(1) h4 a", "Document", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .modified", "MODIFIED: WEDNESDAY, JANUARY 6, 2010", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .creator-tag", "Test User", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a.creator-tag", "Test User", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a:nth-of-type(3)", "Tag", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a:nth-of-type(4)", "With space", true, true);
  }
  
  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testMostRecentAnonymous() {
    navigate("/forge/public");
    waitAndAssertSelectorText(".latest h3", "Latest", true, true);
    assertSelectorCount(".latest .material", 5);
    assertSelectorText(".latest .material:nth-child(1) h4 a", "Google Doc", true, true);
    assertSelectorText(".latest .material:nth-child(1) .modified", "MODIFIED: FRIDAY, JANUARY 22, 2010", true, true);
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testCategoriesAnonymous() {
    navigate("/forge/public");
    waitAndAssertSelectorText(".categories h3", "Categories", true, true);
    assertSelectorCount(".categories .tags li", 3);
    assertSelectorText(".categories .tags li:nth-child(1) a", "test (3)", true, true);
    assertSelectorText(".categories .tags li:nth-child(2) a", "tag (2)", true, true);
    assertSelectorText(".categories .tags li:nth-child(3) a", "with space (1)", true, true);
  }
  
  @Test
  @SqlSets ({"users", "public-materials"})
  public void testRandomMaterialLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/public");
    waitAndAssertSelectorText(".random h3", "Random material", true, true);
    assertSelectorCount(".random .material", 1);
    assertSelectorPresent(".random .material h4 a");
    assertSelectorPresent(".random .material .modified");
    assertSelectorPresent(".random .material .creator-tag");
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testMostPopularLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/public");
    waitAndAssertSelectorText(".most-popular h3", "Most Popular", true, true);
    assertSelectorCount(".most-popular .material", 5);
    assertSelectorText(".most-popular .material:nth-child(1) h4 a", "Document", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .modified", "MODIFIED: WEDNESDAY, JANUARY 6, 2010", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .creator-tag", "Test User", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a.creator-tag", "Test User", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a:nth-of-type(3)", "Tag", true, true);
    assertSelectorText(".most-popular .material:nth-child(1) .material-tags a:nth-of-type(4)", "With space", true, true);
  }
  
  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testMostRecentLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/public");
    waitAndAssertSelectorText(".latest h3", "Latest", true, true);
    assertSelectorCount(".latest .material", 5);
    assertSelectorText(".latest .material:nth-child(1) h4 a", "Google Doc", true, true);
    assertSelectorText(".latest .material:nth-child(1) .modified", "MODIFIED: FRIDAY, JANUARY 22, 2010", true, true);
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testCategoriesLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/public");
    waitAndAssertSelectorText(".categories h3", "Categories", true, true);
    assertSelectorCount(".categories .tags li", 3);
    assertSelectorText(".categories .tags li:nth-child(1) a", "test (3)", true, true);
    assertSelectorText(".categories .tags li:nth-child(2) a", "tag (2)", true, true);
    assertSelectorText(".categories .tags li:nth-child(3) a", "with space (1)", true, true);
  }
}
