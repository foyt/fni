package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionCreateEventTestsBase;

public class IllusionCreateEventTestsIT extends IllusionCreateEventTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}