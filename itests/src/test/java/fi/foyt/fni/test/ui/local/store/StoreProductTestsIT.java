package fi.foyt.fni.test.ui.local.store;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.store.StoreProductTestsBase;

public class StoreProductTestsIT extends StoreProductTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}