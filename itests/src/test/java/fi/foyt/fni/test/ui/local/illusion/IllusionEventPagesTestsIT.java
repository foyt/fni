package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventPagesTestsBase;

public class IllusionEventPagesTestsIT extends IllusionEventPagesTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}