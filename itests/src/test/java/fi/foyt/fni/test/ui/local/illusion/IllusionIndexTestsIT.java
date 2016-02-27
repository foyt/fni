package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionIndexTestsBase;

public class IllusionIndexTestsIT extends IllusionIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}