package fi.foyt.fni.test.ui.base.forge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql")
})
public class ForgeConnectDropboxTestsBase extends AbstractUITest {
  
  @Before
  public void baseSetUp() throws Exception {
    createOAuthSettings();
  }
  
  @After
  public void baseTearDown() throws Exception {
    purgeOAuthSettings();
  }
  
  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/forge/connect-dropbox");
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testConnect() {
    if ("phantomjs".equals(getBrowser())) {
      // TODO: Dropbox requires a CAPTCHA on phantomjs
      return;
    }
    
    loginGoogle();
    navigate("/forge/");
    waitAndClick(".forge-import-material-menu");
    waitAndClick(".forge-import-material-menu .forge-connect-dropbox");
    
    waitForUrlMatches("^https://www.dropbox.com/1/oauth/authorize.*");
   
    waitAndClick("input[type=\"email\"]");
    typeSelectorInputValue("input[type=\"email\"]", getDropboxUsername());
    waitAndClick("input[type=\"password\"]");
    typeSelectorInputValue("input[type=\"password\"]", getDropboxPassword());
    waitAndClick(".login-button");
    waitAndClick("*[name='allow_access']");
    waitTitle("Forge");
    assertTitle("Forge");
    waitForNotification();
    assertNotificationStartsWith("info", "Dropbox folder is connected");
    waitForSelectorCount(".forge-material-title[title=\"Dropbox\"]", 2);
    assertSelectorCount(".forge-material-title[title=\"Dropbox\"]", 2);
  }
  
}
