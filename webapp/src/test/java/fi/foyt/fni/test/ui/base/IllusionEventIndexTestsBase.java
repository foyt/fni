package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.SqlAfter;
import fi.foyt.fni.test.SqlBefore;

public class IllusionEventIndexTestsBase extends AbstractUITest {
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testNotLoggedIn() throws Exception {
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation a", 1);
    assertSelectorPresent(".illusion-event-join-button");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testNotFound() throws Exception {
    testNotFound("/illusion/event/openevent/");
    testNotFound("/illusion/event/noevent");
    testNotFound("/illusion/event/noevent/");
    testNotFound("/illusion/event/noevent/*");
    testNotFound("/illusion/event/1");
    testNotFound("/illusion/event//");
    testNotFound("/illusion/event//*");
    testNotFound("/illusion/event/~");
  }

  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql"})
  @SqlAfter ({"illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedIn() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation a", 1);
    assertSelectorPresent(".illusion-event-join-button");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql"})
  @SqlAfter ({ "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedInParticipant() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation a", 2);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql"})
  @SqlAfter ({ "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoggedInOrganizer() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation a", 5);
    assertSelectorNotPresent(".illusion-event-join-button");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "front page");
  }
  

}
