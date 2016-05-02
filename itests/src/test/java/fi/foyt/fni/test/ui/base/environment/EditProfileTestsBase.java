package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql")
})
public class EditProfileTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users"})
  public void testTitle() {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/editprofile", "Edit Profile");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/editprofile");
  }

  @Test
  @SqlSets ({"basic-users"})
  public void testGuest() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied("/editprofile");
  }

  @Test
  @SqlSets ({"basic-users"})
  public void testUser() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/editprofile");
    testTitle("Edit Profile");
  }

  @Test
  @SqlSets ({"basic-users"})
  public void testLibrarian() throws Exception {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }

  @Test
  @SqlSets ({"basic-users"})
  public void testAdministrator() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/editprofile");
    assertEquals("Edit Profile", getWebDriver().getTitle());
  }
  
  @Test
  public void testChangeInternalPasswordRequired() throws Exception {
    acceptCookieDirective();
    
    createUser(7l, "Password", "Change", "passchange@foyt.fi", "oldpass", "en", "GRAVATAR", "USER");
    try {
      loginInternal("passchange@foyt.fi", "oldpass");
      getWebDriver().get(getAppUrl() + "/editprofile");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password")).click();
      waitForSelectorVisible(".users-editprofile-authentication-source-change-password-container");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container input[type=\"submit\"]")).click();
      waitForNotification();
      assertNotification("warning", "Password is required");
    } finally {
      deleteUser(7l);
    }
  } 

  @Test
  public void testChangeInternalPasswordNoMatch() throws Exception {
    acceptCookieDirective();
    
    createUser(7l, "Password", "Change", "passchange@foyt.fi", "oldpass", "en", "GRAVATAR", "USER");
    try {
      loginInternal("passchange@foyt.fi", "oldpass");
      getWebDriver().get(getAppUrl() + "/editprofile");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password")).click();
      waitForSelectorVisible(".users-editprofile-authentication-source-change-password-container");

      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-password1")).sendKeys("qwe");
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-password2")).sendKeys("asd");
      
      getWebDriver().findElement(By.cssSelector(".users-editprofile-authentication-source-change-password-container input[type=\"submit\"]")).click();
      waitForNotification();
      assertNotification("warning", "Passwords do not match");
    } finally {
      deleteUser(7l);
    }
  } 
  
  @Test
  public void testChangeInternalPassword() throws Exception {
    acceptCookieDirective();
    
    createUser(7l, "Password", "Change", "passchange@foyt.fi", "oldpass", "en", "GRAVATAR", "USER");
    try {
      loginInternal("passchange@foyt.fi", "oldpass");
      navigate("/editprofile");
      scrollWaitAndClick(".users-editprofile-authentication-source-change-password");
      waitForSelectorVisible(".users-editprofile-authentication-source-change-password-container");
      typeSelectorInputValue(".users-editprofile-authentication-source-change-password-password1", "qwe");
      typeSelectorInputValue(".users-editprofile-authentication-source-change-password-password2", "qwe");
      scrollWaitAndClick(".users-editprofile-authentication-source-change-password-container input[type=\"submit\"]");
      waitForNotification();
      assertNotification("info", "Password Changed");
      loginInternal("passchange@foyt.fi", "qwe");
    } finally {
      deleteUser(7l);
    }
  } 
    
}
