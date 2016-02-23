package fi.foyt.fni.test.ui.local.forum;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forum.ForumPostEditTestsBase;

public class ForumPostEditTestsIT extends ForumPostEditTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}