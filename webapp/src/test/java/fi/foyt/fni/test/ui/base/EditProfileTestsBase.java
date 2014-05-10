package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EditProfileTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/editprofile", "Edit Profile");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/editprofile");
  }

  @Test
  public void testGuest() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/editprofile");
  }

  @Test
  public void testUser() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }

  @Test
  public void testLibrarian() throws Exception {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }

  @Test
  public void testAdministrator() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }

}
