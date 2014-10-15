package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.SqlAfter;
import fi.foyt.fni.test.SqlBefore;

public class IllusionEventSettingsTestsBase extends AbstractUITest {
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"})
  @SqlAfter ({ "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/settings/");
    testNotFound("/illusion/event/noevent/settings");
    testNotFound("/illusion/event/noevent//settings");
    testNotFound("/illusion/event/noevent/*/settings");
    testNotFound("/illusion/event/1/settings");
    testNotFound("/illusion/event///settings");
    testNotFound("/illusion/event//*/settings");
    testNotFound("/illusion/event/~/settings");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql" })
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql"})
  @SqlAfter ({ "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testAccessDeniedParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"})
  @SqlAfter ({ "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/settings", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation a", 5);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "settings");
  }
  
}
