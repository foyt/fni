package fi.foyt.fni.test.ui.local.gamelibrary;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryCartTestsBase;

public class GameLibraryCartTestsIT extends GameLibraryCartTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}