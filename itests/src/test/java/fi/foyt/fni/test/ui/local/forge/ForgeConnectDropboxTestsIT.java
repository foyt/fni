package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forge.ForgeConnectDropboxTestsBase;

public class ForgeConnectDropboxTestsIT extends ForgeConnectDropboxTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }
  
}
