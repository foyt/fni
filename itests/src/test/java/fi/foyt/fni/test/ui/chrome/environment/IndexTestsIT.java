package fi.foyt.fni.test.ui.chrome.environment;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriver;

import fi.foyt.fni.test.ui.base.environment.IndexTestsBase;

public class IndexTestsIT extends IndexTestsBase {

  @Before
  public void setUp() {
    setWebDriver(new ChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}