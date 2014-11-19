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
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", "10/20/2030");
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
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", "10/20/2030");
    
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();

    waitForUrlMatches(".*/illusion/event/" + urlName);
    testTitle("Illusion - name");
    
    assertSelectorTextIgnoreCase(".view-header-description-title", name);
    assertSelectorTextIgnoreCase(".view-header-description-text", description);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    
    deleteIllusionEventByUrl(urlName);
  }
  
  @Test
  public void testCreateEventWithStartDate() throws Exception {
    loginInternal("admin@foyt.fi", "pass");

    String name = "withstart";
    String urlName = "withstart";
    String description = "withstart";
    String startDate = "10/20/2030";
    
    navigate("/illusion/createevent");
    
    typeSelectorInputValue(".illusion-create-event-name", name);
    typeSelectorInputValue(".illusion-create-event-description", description);
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();

    waitForUrlMatches(".*/illusion/event/" + urlName);
    testTitle("Illusion - " + name);
    
    assertSelectorTextIgnoreCase(".view-header-description-title", name);
    assertSelectorTextIgnoreCase(".view-header-description-text", description);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    
    navigate("/illusion/event/" + urlName + "/settings");
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    
    deleteIllusionEventByUrl(urlName);
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
    typeSelectorInputValue(".illusion-create-event-name", name);
    typeSelectorInputValue(".illusion-create-event-description", description);
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    typeSelectorInputValue("input[data-alt-field='.actual-start-time']", startTime);
    typeSelectorInputValue("input[data-alt-field='.actual-end-date']", endDate);
    typeSelectorInputValue("input[data-alt-field='.actual-end-time']", endTime);
    
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

    deleteIllusionEventByUrl(urlName);
  }
  
  @Test
  public void testCreateEventWithLocation() throws Exception {
    loginInternal("admin@foyt.fi", "pass");

    String name = "timesanddates";
    String urlName = "timesanddates";
    String description = "timesanddates";
    String location = "location";
    String startDate = "10/20/2030";
    
    navigate("/illusion/createevent");
    typeSelectorInputValue(".illusion-create-event-name", name);
    typeSelectorInputValue(".illusion-create-event-description", description);
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    typeSelectorInputValue(".illusion-create-event-location", location);
    waitSelectorToBeClickable(".illusion-create-event-save");
    findElementBySelector(".illusion-create-event-save").click();
    waitForUrlMatches(".*/illusion/event/" + urlName);
    navigate("/illusion/event/" + urlName + "/settings");
    assertSelectorValue(".illusion-event-settings-location", location);
    
    deleteIllusionEventByUrl(urlName);
  }

  private void deleteIllusionEventByUrl(String urlName) throws Exception {
    executeSql("delete from IllusionEventParticipant where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("update Material set type = 'DELETE' where id in (select folder_id from IllusionEvent where urlName = ?) or parentFolder_id in (select folder_id from IllusionEvent where urlName = ?)", urlName, urlName);
    executeSql("delete from IllusionEvent where urlName = ?", urlName);
    executeSql("update Material set parentFolder_id = null where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from IllusionEventDocument where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from IllusionEventFolder where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from Document where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from Folder where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from Material where type = 'DELETE'");
  }
  
}
