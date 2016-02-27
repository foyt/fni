package fi.foyt.fni.test.ui.local.store;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.store.StoreListTestsBase;

public class StoreListTestsIT extends StoreListTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}