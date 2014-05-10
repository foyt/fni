package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class GameLibraryEditPublicationTestsBase extends AbstractUITest {

  private static final Long PUBLICATION_ID = 1l;
  private static final String TEST_URL = "/gamelibrary/manage/" + PUBLICATION_ID + "/edit";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), TEST_URL, true);
  }

  @Test
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), TEST_URL, true);
  }

  @Test
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/gamelibrary/manage/¨/edit");
    testNotFound(getWebDriver(), "/gamelibrary/manage/-1/edit");
    testNotFound(getWebDriver(), "/gamelibrary/manage//edit");
    testNotFound(getWebDriver(), "/gamelibrary/manage/asd/edit");
  }

  @Test
  public void testLibrarian() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testTitle(getWebDriver(), TEST_URL, "Edit Publication: Fat hag dwarves quickly zap jinx mob");
  }

  @Test
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testTitle(getWebDriver(), TEST_URL, "Edit Publication: Fat hag dwarves quickly zap jinx mob");
  }

}
