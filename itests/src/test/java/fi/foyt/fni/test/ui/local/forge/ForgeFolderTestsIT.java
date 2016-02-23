package fi.foyt.fni.test.ui.local.forge;

import org.junit.Before;

import fi.foyt.fni.test.ui.base.forge.ForgeFolderTestsBase;

public class ForgeFolderTestsIT extends ForgeFolderTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }
  
}
