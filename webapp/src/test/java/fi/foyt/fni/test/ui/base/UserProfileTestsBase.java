package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class UserProfileTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    testTitle(getWebDriver(), "/profile/1", "User Profile");
  }

  @Test
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/profile/~");
    testNotFound(getWebDriver(), "/profile/12345");
    testNotFound(getWebDriver(), "/profile/-1");
    testNotFound(getWebDriver(), "/profile/");
    testNotFound(getWebDriver(), "/profile/asd");
  }

}
