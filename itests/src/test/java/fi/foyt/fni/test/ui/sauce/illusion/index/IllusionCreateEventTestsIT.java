package fi.foyt.fni.test.ui.sauce.illusion.index;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.illusion.IllusionCreateEventTestsBase;

public class IllusionCreateEventTestsIT extends IllusionCreateEventTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @Override
  @SqlSets ("illusion-basic")
  public void testCreateEventType() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Microsoft edge driver does not support checking checkboxes
      return;
    }
    
    super.testCreateEventType();
  }
  
  @Override
  @SqlSets ("illusion-basic")
  public void testEventLarpKalenteriCreateLocation() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Test does not work on edge, needs more investigation
      return;
    }
    
    super.testEventLarpKalenteriCreateLocation();
  }

}