package fi.foyt.fni.test.ui.sauce.environment;

import java.net.MalformedURLException;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.environment.LoginTestsBase;

public class LoginTestsIT extends LoginTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @Override
  public void testResetPasswordIncorrectEmail() {
    if ("microsoftedge".equals(getWebDriver())) {
      // FIXME: Edge window is too small for this test and the driver does not support window resize just yet
      return;
    }
    
    super.testResetPasswordIncorrectEmail();
  }

  @Override
  @SqlSets ("basic")
  public void testResetPasswordInvalidEmail() {
    if ("microsoftedge".equals(getWebDriver())) {
      // FIXME: Edge window is too small for this test and the driver does not support window resize just yet
      return;
    }
    
    super.testResetPasswordInvalidEmail();
  }
  
  @Override
  @SqlSets ("basic")
  public void testResetPassword() throws MessagingException {
    if ("microsoftedge".equals(getWebDriver())) {
      // FIXME: Edge window is too small for this test and the driver does not support window resize just yet
      return;
    }
    
    super.testResetPassword();
  }

}