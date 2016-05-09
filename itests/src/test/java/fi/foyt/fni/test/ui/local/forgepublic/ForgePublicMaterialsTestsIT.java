package fi.foyt.fni.test.ui.local.forgepublic;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forgepublic.ForgePublicMaterialsTestsBase;

public class ForgePublicMaterialsTestsIT extends ForgePublicMaterialsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}