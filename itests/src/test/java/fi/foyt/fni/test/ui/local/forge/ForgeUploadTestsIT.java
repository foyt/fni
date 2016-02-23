package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeUploadTestsBase;

public class ForgeUploadTestsIT extends ForgeUploadTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}