package fi.foyt.fni.test.ui.local.gamelibrary;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryListTestsBase;

public class GameLibraryListTestsIT extends GameLibraryListTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}