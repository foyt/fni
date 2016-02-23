package fi.foyt.fni.test.ui.local.forum;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.forum.ForumIndexTestsBase;

public class ForumIndexTestsIT extends ForumIndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}