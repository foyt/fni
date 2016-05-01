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
public class ForgePublicMaterialsTestsBase extends AbstractUITest {

  @Test
  public void testTitleTags() {
    testTitle("/forge/public/materials/?tags=test", "Forge Public - Test");
  }
  
  @Test
  @SqlSets ({"users", "public-materials"})
  public void testTitleAuthor() {
    testTitle("/forge/public/materials/?userId=2", "Forge Public - Test User");
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testAuthorAnonymous() {
    navigate("/forge/public/materials/?userId=2");
    waitForSelectorPresent(".forge-public-materials");
    assertSelectorCount(".forge-public-materials .material", 17);
    assertSelectorText(".forge-public-materials .material:nth-child(1) h4 a", "Document", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .modified", "MODIFIED: WEDNESDAY, JANUARY 6, 2010", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a.creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(3)", "Tag", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(4)", "With space", true, true);
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testAuthorLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/public/materials/?userId=2");
    waitForSelectorPresent(".forge-public-materials");
    assertSelectorCount(".forge-public-materials .material", 17);
    assertSelectorText(".forge-public-materials .material:nth-child(1) h4 a", "Document", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .modified", "MODIFIED: WEDNESDAY, JANUARY 6, 2010", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a.creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(3)", "Tag", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(4)", "With space", true, true);
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testTagAnonymous() {
    navigate("/forge/public/materials/?tags=test");
    waitForSelectorPresent(".forge-public-materials");
    assertSelectorCount(".forge-public-materials .material", 3);
    assertSelectorText(".forge-public-materials .material:nth-child(1) h4 a", "Document in folder", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .modified", "MODIFIED: THURSDAY, JANUARY 7, 2010", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a.creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(3)", "Tag", true, true);
  }

  @Test
  @SqlSets ({"users", "public-materials", "public-material-tags"})
  public void testTagLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/public/materials/?tags=test");
    waitForSelectorPresent(".forge-public-materials");
    assertSelectorCount(".forge-public-materials .material", 3);
    assertSelectorText(".forge-public-materials .material:nth-child(1) h4 a", "Document in folder", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .modified", "MODIFIED: THURSDAY, JANUARY 7, 2010", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a.creator-tag", "Test User", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".forge-public-materials .material:nth-child(1) .material-tags a:nth-of-type(3)", "Tag", true, true);
  }

}
