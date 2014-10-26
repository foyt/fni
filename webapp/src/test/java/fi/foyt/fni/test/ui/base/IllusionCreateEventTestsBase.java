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
    navigate("/illusion/createevent");
    setSelectorInputValue("input[data-alt-field='.actual-start-date']", "10/20/2030");
    findElementBySelector(".illusion-create-event-save").click();
    waitForNotification();
    assertNotification("error", "Name is required");
  }
  
  @Test
  public void testCreateEvent() throws Exception {
    loginInternal("admin@foyt.fi", "pass");

    String name = "name";
    String urlName = "name";
    String description = "description";

    navigate("/illusion/createevent");
    
    findElementBySelector(".illusion-create-event-name").sendKeys(name);
    findElementBySelector(".illusion-create-event-description").sendKeys(description);
    setSelectorInputValue("input[data-alt-field='.actual-start-date']", "10/20/2030");
    
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();

    waitForUrlMatches(".*/illusion/event/" + urlName);
    testTitle("Illusion - name");
    
    assertSelectorTextIgnoreCase(".view-header-description-title", name);
    assertSelectorTextIgnoreCase(".view-header-description-text", description);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    
    executeSql("delete from IllusionEventParticipant where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("delete from IllusionEvent where urlName = ?", urlName);
  }
  
  @Test
  public void testCreateEventWithStartDate() throws Exception {
    loginInternal("admin@foyt.fi", "pass");

    String name = "withstart";
    String urlName = "withstart";
    String description = "withstart";
    String startDate = "10/20/2030";
    
    navigate("/illusion/createevent");
    
    setSelectorInputValue(".illusion-create-event-name", name);
    setSelectorInputValue(".illusion-create-event-description", description);
    setSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();

    waitForUrlMatches(".*/illusion/event/" + urlName);
    testTitle("Illusion - " + name);
    
    assertSelectorTextIgnoreCase(".view-header-description-title", name);
    assertSelectorTextIgnoreCase(".view-header-description-text", description);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    
    navigate("/illusion/event/" + urlName + "/settings");
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    
    executeSql("delete from IllusionEventParticipant where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("delete from IllusionEvent where urlName = ?", urlName);
  }
  
  @Test
  public void testCreateEventWithTimesAndDates() throws Exception {
    loginInternal("admin@foyt.fi", "pass");

    String name = "timesanddates";
    String urlName = "timesanddates";
    String description = "timesanddates";
    String startDate = "10/20/2030";
    String startTime = "12:00";
    String endDate = "11/21/2031";
    String endTime = "10:30";
    
    navigate("/illusion/createevent");
    setSelectorInputValue(".illusion-create-event-name", name);
    setSelectorInputValue(".illusion-create-event-description", description);
    setSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    setSelectorInputValue("input[data-alt-field='.actual-start-time']", startTime);
    setSelectorInputValue("input[data-alt-field='.actual-end-date']", endDate);
    setSelectorInputValue("input[data-alt-field='.actual-end-time']", endTime);
    
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();

    waitForUrlMatches(".*/illusion/event/" + urlName);
    testTitle("Illusion - " + name);
    
    assertSelectorTextIgnoreCase(".view-header-description-title", name);
    assertSelectorTextIgnoreCase(".view-header-description-text", description);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    
    navigate("/illusion/event/" + urlName + "/settings");
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    assertSelectorValue("input[data-alt-field='.actual-start-time']", startTime);
    assertSelectorValue("input[data-alt-field='.actual-end-date']", endDate);
    assertSelectorValue("input[data-alt-field='.actual-end-time']", endTime);
    
    executeSql("delete from IllusionEventParticipant where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("delete from IllusionEvent where urlName = ?", urlName);
  }
  
}
