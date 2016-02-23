package fi.foyt.fni.test.ui.local.forum;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forum.ForumTopicTestsBase;

public class ForumTopicTestsIT extends ForumTopicTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}