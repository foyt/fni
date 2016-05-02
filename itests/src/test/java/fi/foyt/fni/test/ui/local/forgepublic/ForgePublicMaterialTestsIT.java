package fi.foyt.fni.test.ui.local.forgepublic;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forgepublic.ForgePublicMaterialTestsBase;

public class ForgePublicMaterialTestsIT extends ForgePublicMaterialTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}
