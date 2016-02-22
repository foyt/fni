package fi.foyt.fni.test.ui.local.illusion;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventManageTemplatesTestsBase;

public class IllusionEventManageTemplatesTestsIT extends IllusionEventManageTemplatesTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}