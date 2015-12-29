package fi.foyt.fni.test.ui.chrome.store;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.store.StoreManageTestsBase;

public class StoreManageTestsIT extends StoreManageTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}