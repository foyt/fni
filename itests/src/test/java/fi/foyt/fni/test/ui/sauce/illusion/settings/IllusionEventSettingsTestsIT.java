package fi.foyt.fni.test.ui.sauce.illusion.settings;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventSettingsTestsBase;

public class IllusionEventSettingsTestsIT extends IllusionEventSettingsTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @Override
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventType() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Microsoft edge driver does not support checking checkboxes
      return;
    }
    
    super.testEventType();
  }
  
  @Override
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testLocation() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Test does not work on edge, needs more investigation
      return;
    }
    
    super.testLocation();
  }
   
}