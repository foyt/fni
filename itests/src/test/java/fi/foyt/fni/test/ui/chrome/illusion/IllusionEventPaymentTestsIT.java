package fi.foyt.fni.test.ui.chrome.illusion;

import org.junit.After;
import org.junit.Before;

import fi.foyt.fni.test.ui.base.illusion.IllusionEventPaymentTestsBase;

public class IllusionEventPaymentTestsIT extends IllusionEventPaymentTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}