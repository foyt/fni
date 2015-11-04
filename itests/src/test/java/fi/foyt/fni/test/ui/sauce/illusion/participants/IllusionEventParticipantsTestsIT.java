package fi.foyt.fni.test.ui.sauce.illusion.participants;

import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventParticipantsTestsBase;

public class IllusionEventParticipantsTestsIT extends IllusionEventParticipantsTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @After
  public void tearDown() {
    getWebDriver().quit();
  }
  
  @Override
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-participant", "illusion-event-organizer"})
  public void testUpdateRole() {
    if ("microsoftedge".equals(getWebDriver())) {
      // FIXME: Microsoft edge driver does not support checking checkboxes
      return;
    }
    
    super.testUpdateRole();
  }
  
}