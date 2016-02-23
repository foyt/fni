package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeVectorImageTestsBase;

public class ForgeVectorImageTestsIT extends ForgeVectorImageTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}