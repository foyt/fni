package fi.foyt.fni.test.ui.chrome.gamelibrary;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryOrderTestsBase;

public class GameLibraryOrderTestsIT extends GameLibraryOrderTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}