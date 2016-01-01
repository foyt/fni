package fi.foyt.fni.test.ui.chrome.forge;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeGoogleDriveTestsBase;

public class ForgeGoogleDriveTestsIT extends ForgeGoogleDriveTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}