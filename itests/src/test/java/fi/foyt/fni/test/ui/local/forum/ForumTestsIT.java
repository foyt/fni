package fi.foyt.fni.test.ui.local.forum;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forum.ForumTestsBase;

public class ForumTestsIT extends ForumTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}