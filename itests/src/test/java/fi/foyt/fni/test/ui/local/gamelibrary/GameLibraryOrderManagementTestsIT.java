package fi.foyt.fni.test.ui.local.gamelibrary;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryOrderManagementTestsBase;

public class GameLibraryOrderManagementTestsIT extends GameLibraryOrderManagementTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}