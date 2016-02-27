package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forge.ForgeGoogleDriveTestsBase;

public class ForgeGoogleDriveTestsIT extends ForgeGoogleDriveTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }
  
}