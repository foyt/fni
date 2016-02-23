package fi.foyt.fni.test.ui.local.illusion;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventParticipantsTestsBase;

public class IllusionEventParticipantsTestsIT extends IllusionEventParticipantsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}