package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventForumTestsBase;

public class IllusionEventForumTestsIT extends IllusionEventForumTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}