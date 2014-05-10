package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class GameLibraryOrderManagementTestsBase extends AbstractUITest {

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/gamelibrary/ordermanagement/", true);
  }

  @Test
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/gamelibrary/ordermanagement/", true);
  }

  @Test
  public void testLibrarian() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testTitle(getWebDriver(), "/gamelibrary/ordermanagement/", "Game Library - Order Management");
  }

  @Test
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testTitle(getWebDriver(), "/gamelibrary/ordermanagement/", "Game Library - Order Management");
  }

}
