package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventMaterialTestsBase;

public class IllusionEventMaterialTestsIT extends IllusionEventMaterialTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}