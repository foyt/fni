package fi.foyt.fni.test.ui.base;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class IllusionCreateEventTestsBase extends AbstractUITest {

  @Test
  public void testLoginRedirect() throws UnsupportedEncodingException {
    testLoginRequired("/illusion/createevent");
  }

  @Test
  public void testTitleAdmin() {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/createevent", "Create Event");
  }

  @Test
  public void testTitleUser() {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/illusion/createevent", "Create Event");
  }
  
  @Test
  public void testNameRequired() {
    loginInternal("admin@foyt.fi", "pass");
    getPath("/illusion/createevent");
    findElementBySelector(".illusion-create-event-save").click();
    waitForNotification();
    assertNotification("error", "Name is required");
  }
  
  @Test
  public void testCreateEvent() {
    loginInternal("admin@foyt.fi", "pass");

    String name = "name";
    String description = "description";

    getPath("/illusion/createevent");
    
    findElementBySelector(".illusion-create-event-name").sendKeys(name);
    findElementBySelector(".illusion-create-event-description").sendKeys(description);
    
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();

    waitForUrlNotMatches(".*/illusion/createevent");
    
    assertSelectorTextIgnoreCase(".view-header-description-title", name);
    assertSelectorTextIgnoreCase(".view-header-description-text", description);
  }

}
