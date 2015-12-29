package fi.foyt.fni.test.ui.chrome.gamelibrary;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryOrderManagementTestsBase;

public class GameLibraryOrderManagementTestsIT extends GameLibraryOrderManagementTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}