package fi.foyt.fni.test.ui.sauce.environment;

import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.environment.LoginTestsBase;
import fi.foyt.fni.test.ui.sauce.SauceLabsUtils;

@RunWith (Parameterized.class)
public class LoginTestsIT extends LoginTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Parameterized.Parameters
  public static List<String[]> browsers() throws Exception {
    return SauceLabsUtils.getSauceBrowsers();
  }

  public LoginTestsIT(String browser, String version, String platform) {
    this.browser = browser;
    this.version = version;
    this.platform = platform;
  }
  
  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver(browser, version, platform));
  }
  
  @After
  public void tearDown() {
    getWebDriver().quit();
  }
  
  @Override
  public void testResetPasswordIncorrectEmail() {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge window is too small for this test and the driver does not support window resize just yet
      return;
    }
    
    super.testResetPasswordIncorrectEmail();
  }

  @Override
  @SqlSets ("basic")
  public void testResetPasswordInvalidEmail() {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge window is too small for this test and the driver does not support window resize just yet
      return;
    }
    
    super.testResetPasswordInvalidEmail();
  }
  
  @Override
  @SqlSets ("basic")
  public void testResetPassword() throws MessagingException {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge window is too small for this test and the driver does not support window resize just yet
      return;
    }
    
    super.testResetPassword();
  }
  
  private String platform;
  private String browser;
  private String version;  
}