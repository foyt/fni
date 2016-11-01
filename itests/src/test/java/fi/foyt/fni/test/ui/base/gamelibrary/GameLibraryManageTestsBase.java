package fi.foyt.fni.test.ui.base.gamelibrary;

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
    loginInternal("librarian@foyt.fi", "pass");
    testTitle("/gamelibrary/manage/", "Game Library - Management");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/gamelibrary/manage/");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testUnauthorized() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/gamelibrary/manage/");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLibrarian() throws Exception {
    loginInternal("librarian@foyt.fi", "pass");
    navigate("/gamelibrary/manage/");
    waitTitle("Game Library - Management");
    assertTitle("Game Library - Management");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAdmin() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/gamelibrary/manage/");
    waitTitle("Game Library - Management");
    assertTitle("Game Library - Management");
  }

}
