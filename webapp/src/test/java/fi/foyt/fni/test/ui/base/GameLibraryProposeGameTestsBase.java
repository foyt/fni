package fi.foyt.fni.test.ui.base;

import org.junit.Test;
import org.openqa.selenium.By;
import static org.junit.Assert.assertEquals;

public class GameLibraryProposeGameTestsBase extends AbstractUITest {

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/gamelibrary/proposegame/", true);
  }
  
  @Test
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/gamelibrary/proposegame/", "Forge & Illusion - Game Library");
  }
  
  @Test
  public void testDesc() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/proposegame/");
    assertEquals("PROPOSE A GAME TO THE LIBRARY", getWebDriver().findElement(By.cssSelector(".view-header-description-title")).getText());
  }

}
