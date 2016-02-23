package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.AboutTestsBase;

public class AboutTestsIT extends AboutTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }
  
}
