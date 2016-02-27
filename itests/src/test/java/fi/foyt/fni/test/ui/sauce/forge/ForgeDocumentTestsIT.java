package fi.foyt.fni.test.ui.sauce.forge;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.forge.ForgeDocumentTestsBase;

public class ForgeDocumentTestsIT extends ForgeDocumentTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @Override
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.testMayEdit();
  }
  
  @Override
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.testWithHyphen();
  }
  
  @Override
  @SqlSets ({"basic-materials-users"})
  public void textCreateSharedFolder() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.textCreateSharedFolder();
  }
  
  @Override
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.testMayView();
  }

}
