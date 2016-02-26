package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeIndexTestsBase;

public class ForgeIndexTestsIT extends ForgeIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}