package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forge.ForgeIllusionTestsBase;

public class ForgeIllusionTestsIT extends ForgeIllusionTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}