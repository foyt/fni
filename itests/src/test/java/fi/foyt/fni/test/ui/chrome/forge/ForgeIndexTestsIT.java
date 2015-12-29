package fi.foyt.fni.test.ui.chrome.forge;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeIndexTestsBase;

public class ForgeIndexTestsIT extends ForgeIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}