package fi.foyt.fni.test.ui.base;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginTestsBase extends AbstractUITest {

  @Before
  public void baseSetUp() throws Exception {
    createOAuthSettings();
  }

  @After
  public void baseTearDown() throws Exception {
    purgeOAuthSettings();
  }

  @Test
  public void testTitle() {
    testTitle(getWebDriver(), "/login", "Login");
  }

  @Test
  public void testInternal() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
  }

  @Test
  public void testFacebook() {
    loginFacebook(getWebDriver());
  }

  @Test
  public void testGoogle() {
    loginGoogle(getWebDriver());
  }

}
