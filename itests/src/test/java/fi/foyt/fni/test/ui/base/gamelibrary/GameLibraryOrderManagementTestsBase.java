package fi.foyt.fni.test.ui.base.gamelibrary;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryOrderManagementTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/gamelibrary/ordermanagement/", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testUnauthorized() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/gamelibrary/ordermanagement/", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLibrarian() {
    loginInternal("librarian@foyt.fi", "pass");
    testTitle("/gamelibrary/ordermanagement/", "Game Library - Order Management");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAdmin() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/gamelibrary/ordermanagement/", "Game Library - Order Management");
  }

}
