package fi.foyt.fni.test.ui.chrome.gamelibrary;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryProposeGameTestsBase;

public class GameLibraryProposeGameTestsIT extends GameLibraryProposeGameTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}