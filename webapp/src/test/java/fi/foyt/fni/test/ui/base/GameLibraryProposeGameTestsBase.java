package fi.foyt.fni.test.ui.base;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import static org.junit.Assert.assertEquals;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryProposeGameTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/gamelibrary/proposegame/", true);
  }
  
  @Test
  @SqlSets ("basic-gamelibrary")
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/gamelibrary/proposegame/", "Forge & Illusion - Game Library");
  }
  
  @Test
  @SqlSets ("basic-gamelibrary")
  public void testDesc() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/proposegame/");
    assertEquals("PROPOSE A GAME TO THE LIBRARY", getWebDriver().findElement(By.cssSelector(".view-header-description-title")).getText());
  }

}
