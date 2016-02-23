package fi.foyt.fni.test.ui.local.gamelibrary;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryOrderTestsBase;

public class GameLibraryOrderTestsIT extends GameLibraryOrderTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}