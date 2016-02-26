package fi.foyt.fni.test.ui.local.store;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.store.StoreCartTestsBase;

public class StoreCartTestsIT extends StoreCartTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}