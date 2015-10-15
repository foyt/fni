package fi.foyt.fni.test.ui.base.gamelibrary;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryProposeGameTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/gamelibrary/proposegame/", true);
  }
  
  @Test
  @SqlSets ("basic-gamelibrary")
  public void testTitle() {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/gamelibrary/proposegame/", "Forge & Illusion - Game Library");
  }
  
  @Test
  @SqlSets ("basic-gamelibrary")
  public void testDesc() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/", true);
    waitTitle("Forge & Illusion - Game Library");
    assertSelectorTextIgnoreCase(".view-header-description-title", "PROPOSE A GAME TO THE LIBRARY");
  }

}
