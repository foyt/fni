package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventIndexTestsBase;

public class IllusionEventIndexTestsIT extends IllusionEventIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}