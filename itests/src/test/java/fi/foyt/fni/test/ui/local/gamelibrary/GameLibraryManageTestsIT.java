package fi.foyt.fni.test.ui.local.gamelibrary;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryManageTestsBase;

public class GameLibraryManageTestsIT extends GameLibraryManageTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}