package fi.foyt.fni.test.ui.chrome;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriver;

import fi.foyt.fni.test.ui.base.IllusionEventMaterialsTestsBase;

public class IllusionEventMaterialsTestsIT extends IllusionEventMaterialsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(new ChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}