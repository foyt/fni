package fi.foyt.fni.test.ui.base;

import org.openqa.selenium.remote.RemoteWebDriver;

public class AbstractIllusionUITest extends AbstractUITest {

  private static final String CUSTOM_EVENT_HOST = "custom-test.forgeandillusion.net";
  
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
  
}
