package fi.foyt.fni.test.ui.chrome.forum;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriver;

import fi.foyt.fni.test.ui.base.forum.ForumPostEditTestsBase;

public class ForumPostEditTestsIT extends ForumPostEditTestsBase {

  @Before
  public void setUp() {
    setWebDriver(new ChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}