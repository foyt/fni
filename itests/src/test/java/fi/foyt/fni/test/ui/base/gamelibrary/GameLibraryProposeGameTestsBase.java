package fi.foyt.fni.test.ui.base.gamelibrary;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "basic-forum", before = { "basic-forum-setup.sql" }, after = { "basic-forum-teardown.sql" }),
  @DefineSqlSet (id = "store-products", before = { "basic-gamelibrary-setup.sql" }, after = { "basic-gamelibrary-teardown.sql" })
})
public class GameLibraryProposeGameTestsBase extends AbstractUITest {

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/gamelibrary/proposegame/", true);
  }
  
  @Test
  @SqlSets ("basic-users")
  public void testTitle() {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/gamelibrary/proposegame/", "Forge & Illusion - Game Library");
  }
  
  @Test
  @SqlSets ("basic-users")
  public void testDesc() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/", true);
    waitTitle("Forge & Illusion - Game Library");
    assertSelectorTextIgnoreCase(".view-header-description-title", "PROPOSE A GAME TO THE LIBRARY");
  }

  @Test
  @SqlSets ("basic-users")
  public void testPropose() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/", true);
    
//    name
//    description
//    language
//    tags
//    authors-share
//    license-select
//    creative-commons-derivatives
//    creative-commons-commercial
//    license-other
//    send

  }
}
