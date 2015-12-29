package fi.foyt.fni.test.ui.chrome.store;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.store.StoreProductTestsBase;

public class StoreProductTestsIT extends StoreProductTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}