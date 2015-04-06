package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.After;
import org.openqa.selenium.remote.RemoteWebDriver;

public class AbstractIllusionUITest extends AbstractUITest {

  private static final String CUSTOM_EVENT_HOST = "custom-test.forgeandillusion.net";
  
  @After
  public void noMailWatchers() throws SQLException, Exception {
    assertEquals(new Integer(0), countForumTopicWatchers());
  }
  
  protected String getCustomEventUrl() {
    return "http://" + CUSTOM_EVENT_HOST + ':' + getPortHttp() + '/' + getCtxPath();
  }

  protected void loginCustomEvent(String email, String password) {
    RemoteWebDriver driver = getWebDriver();
    
    if (!driver.getCurrentUrl().matches(".*/login.*")) {
      findElementBySelector(".menu-tools-login").click();
    }
    
    waitForSelectorVisible(".user-login-email");
    typeSelectorInputValue(".user-login-email", email);
    typeSelectorInputValue(".user-login-password", password);
    clickSelector(".user-login-button");
    waitForUrlNotMatches(".*/login.*");
    
    assertSelectorPresent(".menu-tools-account");
    assertSelectorNotPresent(".menu-tools-login");
  }

  protected void deleteIllusionTemplate(String eventUrlName, String templateName) throws Exception {
    executeSql("delete from IllusionEventTemplate where event_id = (select id from IllusionEvent where urlName = ?) and name = ?", eventUrlName, templateName);
  }
}
