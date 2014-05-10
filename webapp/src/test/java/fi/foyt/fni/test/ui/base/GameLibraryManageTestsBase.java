package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GameLibraryManageTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testTitle(getWebDriver(), "/gamelibrary/manage/", "Game Library - Management");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/gamelibrary/manage/", true);
  }

  @Test
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/gamelibrary/manage/", true);
  }

  @Test
  public void testLibrarian() throws Exception {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/manage/");
    assertEquals("Game Library - Management", getWebDriver().getTitle());
  }

  @Test
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/manage/");
    assertEquals("Game Library - Management", getWebDriver().getTitle());
  }

}
