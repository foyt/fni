package fi.foyt.fni.test.ui.base.gamelibrary;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryEditPublicationTestsBase extends AbstractUITest {

  private static final Long PUBLICATION_ID = 1l;
  private static final String TEST_URL = "/gamelibrary/manage/" + PUBLICATION_ID + "/edit";

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), TEST_URL, true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied(TEST_URL, true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testNotFound() throws Exception {
    testNotFound("/gamelibrary/manage/¨/edit");
    testNotFound("/gamelibrary/manage/-1/edit");
    testNotFound("/gamelibrary/manage//edit");
    testNotFound("/gamelibrary/manage/asd/edit");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLibrarian() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testTitle(TEST_URL, "Edit Publication: Fat hag dwarves quickly zap jinx mob");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testTitle(TEST_URL, "Edit Publication: Fat hag dwarves quickly zap jinx mob");
  }

}
