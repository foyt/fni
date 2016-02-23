package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeImportGoogleDriveTestsBase;

public class ForgeImportGoogleDriveTestsIT extends ForgeImportGoogleDriveTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

  
}