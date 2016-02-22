package fi.foyt.fni.test.ui.local.forum;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.forum.ForumPostEditTestsBase;

public class ForumPostEditTestsIT extends ForumPostEditTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}