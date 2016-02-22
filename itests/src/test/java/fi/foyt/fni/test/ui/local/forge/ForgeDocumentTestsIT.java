package fi.foyt.fni.test.ui.local.forge;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.forge.ForgeDocumentTestsBase;

public class ForgeDocumentTestsIT extends ForgeDocumentTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}
