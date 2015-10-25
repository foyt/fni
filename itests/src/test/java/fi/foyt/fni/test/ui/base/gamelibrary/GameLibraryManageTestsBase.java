package fi.foyt.fni.test.ui.base.gamelibrary;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryManageTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testTitle() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testTitle("/gamelibrary/manage/", "Game Library - Management");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/gamelibrary/manage/", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied("/gamelibrary/manage/", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLibrarian() throws Exception {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/manage/");
    assertEquals("Game Library - Management", getWebDriver().getTitle());
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAdmin() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/gamelibrary/manage/", true);
    waitTitle("Game Library - Management");
    assertTitle("Game Library - Management");
  }

}
