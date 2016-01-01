package fi.foyt.fni.test.ui.chrome.illusion;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventEditTemplateTestsBase;

public class IllusionEventEditTemplateTestsIT extends IllusionEventEditTemplateTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}