package fi.foyt.fni.test.ui.chrome.illusion;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventIndexTestsBase;

public class IllusionEventIndexTestsIT extends IllusionEventIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}