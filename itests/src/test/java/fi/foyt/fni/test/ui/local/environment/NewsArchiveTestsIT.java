package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.NewsArchiveTestsBase;

public class NewsArchiveTestsIT extends NewsArchiveTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }
  
}
