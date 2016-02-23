package fi.foyt.fni.test.ui.local.store;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.store.StoreManageTestsBase;

public class StoreManageTestsIT extends StoreManageTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}