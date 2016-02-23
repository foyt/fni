package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forge.ForgeImageTestsBase;

public class ForgeImageTestsIT extends ForgeImageTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}