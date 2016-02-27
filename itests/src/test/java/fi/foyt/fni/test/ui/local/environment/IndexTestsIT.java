package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.IndexTestsBase;

public class IndexTestsIT extends IndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}