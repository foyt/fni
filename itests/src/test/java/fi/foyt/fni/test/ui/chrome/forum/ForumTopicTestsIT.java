package fi.foyt.fni.test.ui.chrome.forum;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.forum.ForumTopicTestsBase;

public class ForumTopicTestsIT extends ForumTopicTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}