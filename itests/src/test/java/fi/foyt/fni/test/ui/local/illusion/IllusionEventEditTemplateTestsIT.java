package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventEditTemplateTestsBase;

public class IllusionEventEditTemplateTestsIT extends IllusionEventEditTemplateTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}