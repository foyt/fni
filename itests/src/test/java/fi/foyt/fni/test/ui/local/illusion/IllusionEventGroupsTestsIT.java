package fi.foyt.fni.test.ui.local.illusion;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventGroupsTestsBase;

public class IllusionEventGroupsTestsIT extends IllusionEventGroupsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}