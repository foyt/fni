package fi.foyt.fni.test.ui.local.forgepublic;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forgepublic.ForgePublicIndexTestsBase;

public class ForgePublicIndexTestsIT extends ForgePublicIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}