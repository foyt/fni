package fi.foyt.fni.test.ui.local.gamelibrary;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryProposeGameTestsBase;

public class GameLibraryProposeGameTestsIT extends GameLibraryProposeGameTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}