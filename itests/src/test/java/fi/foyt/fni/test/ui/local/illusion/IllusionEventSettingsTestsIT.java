package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventSettingsTestsBase;

public class IllusionEventSettingsTestsIT extends IllusionEventSettingsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}