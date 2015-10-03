package fi.foyt.fni.test.ui.base;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fi.foyt.fni.larpkalenteri.Event;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql"  }),
  @DefineSqlSet (id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet (id = "illusion-event", before = {"illusion-event-open-setup.sql"}, after = {"illusion-event-open-teardown.sql"} ),
  @DefineSqlSet (id = "illusion-event-organizer", before = {"illusion-event-open-organizer-setup.sql"}, after = {"illusion-event-open-organizer-teardown.sql"} ),
  @DefineSqlSet (id = "illusion-event-custom", before = {"illusion-event-open-custom-setup.sql"}, after = {"illusion-event-open-custom-teardown.sql"} ),
  @DefineSqlSet (id = "illusion-event-unpublished", before = {"illusion-event-open-unpublished-setup.sql"}, after = {"illusion-event-open-unpublished-teardown.sql"} ),
  @DefineSqlSet (id = "illusion-larp-kalenteri", before = {"illusion-event-open-larp-kalenteri-setup.sql"}, after = {"illusion-event-open-larp-kalenteri-teardown.sql"} )
})
public class IllusionEventSettingsTestsBase extends AbstractIllusionUITest {
  
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9080);
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event"})
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
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
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event"})
  public void testAccessDenied() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testAccessDeniedParticipant() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/settings");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/settings", "Event Settings");
    assertSelectorCount(".illusion-event-navigation-admin-menu.illusion-event-navigation-item-active", 1);
    clickSelector(".illusion-event-navigation-admin-menu");
    assertSelectorCount(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-admin-menu .illusion-event-navigation-item.illusion-event-navigation-item-active", "settings");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testDates() throws Exception {
    acceptCookieDirective(getWebDriver());
    
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    String startDate = "10/20/2030";
    String startTime = "12:00";
    String endDate = "11/21/2031";
    String endTime = "10:30";
    
    assertSelectorValue("input[data-alt-field='.actual-start-date']", "01/01/2010");
    assertSelectorValue("input[data-alt-field='.actual-start-time']", "0:00");
    assertSelectorValue("input[data-alt-field='.actual-end-date']", "01/02/2010");
    assertSelectorValue("input[data-alt-field='.actual-end-time']", "0:00");
    
    clearSelectorInput("input[data-alt-field='.actual-start-date']");
    clearSelectorInput("input[data-alt-field='.actual-start-time']");
    clearSelectorInput("input[data-alt-field='.actual-end-date']");
    clearSelectorInput("input[data-alt-field='.actual-end-time']");
    
    typeSelectorInputValue("input[data-alt-field='.actual-start-date']", startDate);
    typeSelectorInputValue("input[data-alt-field='.actual-start-time']", startTime);
    typeSelectorInputValue("input[data-alt-field='.actual-end-date']", endDate);
    typeSelectorInputValue("input[data-alt-field='.actual-end-time']", endTime);
    
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    assertSelectorValue("input[data-alt-field='.actual-start-time']", startTime);
    assertSelectorValue("input[data-alt-field='.actual-end-date']", endDate);
    assertSelectorValue("input[data-alt-field='.actual-end-time']", endTime);
    
    waitSelectorToBeClickable(".illusion-event-settings-save");
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    assertSelectorValue("input[data-alt-field='.actual-start-date']", startDate);
    assertSelectorValue("input[data-alt-field='.actual-start-time']", startTime);
    assertSelectorValue("input[data-alt-field='.actual-end-date']", endDate);
    assertSelectorValue("input[data-alt-field='.actual-end-time']", endTime);
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testLocation() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String location = "Test place";
    typeSelectorInputValue(".illusion-event-settings-location", location);
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    assertSelectorValue(".illusion-event-settings-location", location);
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomain() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/settings");
    testTitle("Event Settings");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomainLoginRedirect() {
    getWebDriver().get(getCustomEventUrl() + "/settings");
    waitForUrlMatches(".*/login.*");
    loginCustomEvent("admin@foyt.fi", "pass");
    testTitle("Event Settings");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomainMenuItems() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/settings");
    testTitle("Event Settings");
    assertMenuItems();
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-event-custom"})
  public void testCustomDomainNavigationLinks() {
    getWebDriver().get(getCustomEventUrl());
    loginCustomEvent("admin@foyt.fi", "pass");
    getWebDriver().get(getCustomEventUrl() + "/settings");
    testTitle("Event Settings");

    assertEquals(getAppUrl() + "/", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(1)").getAttribute("href"));
    assertEquals(getAppUrl() + "/illusion", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(3)").getAttribute("href"));
    assertEquals(getCustomEventUrl() + "/settings", findElementBySelector(".view-header-navigation .view-header-navigation-item:nth-child(5)").getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testDomain() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String location = "Test place";
    typeSelectorInputValue(".illusion-event-settings-domain", location);
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    assertSelectorValue(".illusion-event-settings-domain", location);
    getWebDriver().get(getCustomEventUrl());
    testTitle("Illusion - Open Event");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventSignUpDates() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String signUpStartDate = "10/05/2030";
    String signUpEndDate = "10/10/2030";
    
    typeSelectorInputValue(".sign-up-start-date", signUpStartDate);
    typeSelectorInputValue(".sign-up-end-date", signUpEndDate);
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorValue(".sign-up-start-date", signUpStartDate);
    assertSelectorValue(".sign-up-end-date", signUpEndDate);
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventImageUrl() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String imageUrl = "http://www.url.to/image.png";

    typeSelectorInputValue(".illusion-event-settings-image-url", imageUrl);
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorValue(".illusion-event-settings-image-url", imageUrl);
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventBeginnerFriendly() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-event-settings-beginner-friendly");
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorPresent(".illusion-event-settings-beginner-friendly:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventAgeLimit() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    String ageLimit = "16";

    typeSelectorInputValue(".illusion-event-settings-age-limit", ageLimit);
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorValue(".illusion-event-settings-age-limit", ageLimit);
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventType() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    selectSelectBoxByValue(".illusion-event-settings-type", "2");
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectBoxValue(".illusion-event-settings-type", "2");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventGenres() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    clickSelector(".illusion-event-settings-genre input[value='1']");
    clickSelector(".illusion-event-settings-genre input[value='3']");
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorPresent(".illusion-event-settings-genre input[value='1']:checked");
    assertSelectorNotPresent(".illusion-event-settings-genre input[value='2']:checked");
    assertSelectorPresent(".illusion-event-settings-genre input[value='3']:checked");
    assertSelectorNotPresent(".illusion-event-settings-genre input[value='4']:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventPublished() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");

    assertSelectorPresent(".illusion-event-settings-published:checked");
    
    clickSelector(".illusion-event-settings-published");
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorNotPresent(".illusion-event-settings-published:checked");
    
    clickSelector(".illusion-event-settings-published");
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    assertSelectorPresent(".illusion-event-settings-published:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventDelete() throws Exception {
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    clickSelector(".illusion-remove-event");
    waitForSelectorVisible(".ui-dialog");
    assertSelectorClickable(".ui-dialog .remove-button");
    clickSelector(".ui-dialog .remove-button");
    waitForUrlNotMatches(".*/illusion/event/openevent/settings");
    testNotFound("/illusion/event/openevent");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-larp-kalenteri"})
  public void testEventLarpKalenteriUpdate() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(12345l, 
        "Open Event", "2",
        null, // getDate(2010, 1, 1), FIXME: These fail on SauceLabs to incorrect timezone 
        null, // getDate(2010, 1, 2), FIXME: These fail on SauceLabs to incorrect timezone 
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));
    
    stubFor(get(urlEqualTo("/rest/events/12345"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
              .registerModule(new JodaModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .writeValueAsString(new Event(12345l, 
                "Open Event", "2", 
                getDate(2010, 1, 1), 
                getDate(2010, 1, 2), 
                null, 
                null, 
                null, 
                8l, 
                "", 
                "", 
                new ArrayList<String>(), 
                "", 
                null, 
                false, 
                null, 
                "Event for automatic testing (Open)", 
                "Test Admin", 
                "admin@foyt.fi", 
                "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
                null, 
                Event.Status.ACTIVE, 
                null, 
                false, 
                false, 
                false, 
                1l)))));

    stubFor(put(urlEqualTo("/rest/events/12345"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
            .registerModule(new JodaModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writeValueAsString(new Event(12345l, 
              "Open Event", "2", 
              getDate(2010, 1, 1), 
              getDate(2010, 1, 2), 
              null, 
              null, 
              null, 
              8l, 
              "", 
              "", 
              new ArrayList<String>(), 
              "", 
              null, 
              false, 
              null, 
              "Event for automatic testing (Open)", 
              "Test Admin", 
              "admin@foyt.fi", 
              "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
              null, 
              Event.Status.ACTIVE, 
              null, 
              false, 
              false, 
              false, 
              1l)))));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-event-settings-save");
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, putRequestedFor(urlEqualTo("/rest/events/12345"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-larp-kalenteri"})
  public void testEventLarpKalenteriUpdateGenres() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(12345l, 
        "Open Event", "2", 
        getDate(2010, 1, 1), 
        getDate(2010, 1, 2), 
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        Arrays.asList("fantasy", "cyberpunk"), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));
    
    stubFor(get(urlEqualTo("/rest/events/12345"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
              .registerModule(new JodaModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .writeValueAsString(new Event(12345l, 
                "Open Event", "2", 
                getDate(2010, 1, 1), 
                getDate(2010, 1, 2), 
                null, 
                null, 
                null, 
                8l, 
                "", 
                "", 
                new ArrayList<String>(), 
                "", 
                null, 
                false, 
                null, 
                "Event for automatic testing (Open)", 
                "Test Admin", 
                "admin@foyt.fi", 
                "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
                null, 
                Event.Status.ACTIVE, 
                null, 
                false, 
                false, 
                false, 
                1l)))));

    stubFor(put(urlEqualTo("/rest/events/12345"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
            .registerModule(new JodaModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writeValueAsString(new Event(12345l, 
              "Open Event", "2", 
              getDate(2010, 1, 1), 
              getDate(2010, 1, 2), 
              null, 
              null, 
              null, 
              8l, 
              "", 
              "", 
              Arrays.asList("fantasy", "cyberpunk"), 
              "", 
              null, 
              false, 
              null, 
              "Event for automatic testing (Open)", 
              "Test Admin", 
              "admin@foyt.fi", 
              "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
              null, 
              Event.Status.ACTIVE, 
              null, 
              false, 
              false, 
              false, 
              1l)))));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    clickSelector(".illusion-event-settings-genre input[value='1']");
    clickSelector(".illusion-event-settings-genre input[value='3']");
    
    clickSelector(".illusion-event-settings-save");    
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, putRequestedFor(urlEqualTo("/rest/events/12345"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-larp-kalenteri"})
  public void testEventLarpKalenteriUpdateLocation() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(12345l, 
        "Open Event", "2", 
        getDate(2010, 1, 1), 
        getDate(2010, 1, 2), 
        null, 
        null, 
        null, 
        2l, 
        "Otakaari 24, 02150 Espoo, Finland", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));
    
    stubFor(get(urlEqualTo("/rest/events/12345"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
              .registerModule(new JodaModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .writeValueAsString(new Event(12345l, 
                "Open Event", "2", 
                getDate(2010, 1, 1), 
                getDate(2010, 1, 2), 
                null, 
                null, 
                null, 
                8l, 
                "", 
                "", 
                new ArrayList<String>(), 
                "", 
                null, 
                false, 
                null, 
                "Event for automatic testing (Open)", 
                "Test Admin", 
                "admin@foyt.fi", 
                "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
                null, 
                Event.Status.ACTIVE, 
                null, 
                false, 
                false, 
                false, 
                1l)))));

    stubFor(put(urlEqualTo("/rest/events/12345"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
            .registerModule(new JodaModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writeValueAsString(new Event(12345l, 
              "Open Event", "2", 
              getDate(2010, 1, 1), 
              getDate(2010, 1, 2), 
              null, 
              null, 
              null, 
              2l, 
              "Otakaari 24, 02150 Espoo, Finland", 
              "", 
              new ArrayList<String>(), 
              "", 
              null, 
              false, 
              null, 
              "Event for automatic testing (Open)", 
              "Test Admin", 
              "admin@foyt.fi", 
              "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
              null, 
              Event.Status.ACTIVE, 
              null, 
              false, 
              false, 
              false, 
              1l)))));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    typeSelectorInputValue(".illusion-event-settings-location", "Otakaari 24, 02150 Espoo, Finland");
    clickSelector(".illusion-event-settings-image-url");
    waitForInputValueNotBlank(".location-lat");
    
    clickSelector(".illusion-event-settings-save");  
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, putRequestedFor(urlEqualTo("/rest/events/12345"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventLarpKalenteriCreate() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(null, 
        "Open Event", "2", 
        getDate(2010, 1, 1), 
        getDate(2010, 1, 2), 
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));

    String responseBody = (new com.fasterxml.jackson.databind.ObjectMapper())
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .writeValueAsString(new Event(123l, 
        "Open Event", "2", 
        getDate(2010, 1, 1), 
        getDate(2010, 1, 2), 
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));

    stubFor(post(urlEqualTo("/rest/events/"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(responseBody)));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-create-event-larp-kalenteri");
    clickSelector(".illusion-event-settings-save");  
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, postRequestedFor(urlEqualTo("/rest/events/"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventLarpKalenteriCreateGenres() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(null, 
        "Open Event", "2", 
        null, // getDate(2010, 1, 1), FIXME: These fail on SauceLabs to incorrect timezone  
        null, // getDate(2010, 1, 2), FIXME: These fail on SauceLabs to incorrect timezone  
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        Arrays.asList("fantasy", "cyberpunk"), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));

    String responseBody = (new com.fasterxml.jackson.databind.ObjectMapper())
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .writeValueAsString(new Event(123l, 
        "Open Event", "2", 
        getDate(2010, 1, 1), 
        getDate(2010, 1, 2), 
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        Arrays.asList("fantasy", "cyberpunk"), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));

    stubFor(post(urlEqualTo("/rest/events/"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(responseBody)));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-event-settings-genre input[value='1']");
    clickSelector(".illusion-event-settings-genre input[value='3']");
    clickSelector(".illusion-create-event-larp-kalenteri");
    
    clickSelector(".illusion-event-settings-save");  
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, postRequestedFor(urlEqualTo("/rest/events/"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }
  
  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer"})
  public void testEventLarpKalenteriCreateLocation() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(null, 
        "Open Event", "2", 
        null, // getDate(2010, 1, 1), FIXME: These fail on SauceLabs to incorrect timezone  
        null, // getDate(2010, 1, 2), FIXME: These fail on SauceLabs to incorrect timezone  
        null, 
        null, 
        null, 
        2l, 
        "Otakaari 24, 02150 Espoo, Finland", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));

    String responseBody = (new com.fasterxml.jackson.databind.ObjectMapper())
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .writeValueAsString(new Event(123l, 
        "Open Event", "2", 
        getDate(2010, 1, 1), 
        getDate(2010, 1, 2), 
        null, 
        null, 
        null, 
        2l, 
        "Otakaari 24, 02150 Espoo, Finland", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));

    stubFor(post(urlEqualTo("/rest/events/"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(responseBody)));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    typeSelectorInputValue(".illusion-event-settings-location", "Otakaari 24, 02150 Espoo, Finland");
    clickSelector(".illusion-create-event-larp-kalenteri");
    waitForInputValueNotBlank(".location-lat");
    
    clickSelector(".illusion-event-settings-save");  
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, postRequestedFor(urlEqualTo("/rest/events/"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-unpublished", "illusion-event-organizer", "illusion-larp-kalenteri"})
  public void testEventLarpKalenteriUpdatePublish() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(12345l, 
        "Open Event", "2", 
        null, // getDate(2010, 1, 1), FIXME: These fail on SauceLabs to incorrect timezone  
        null, // getDate(2010, 1, 2), FIXME: These fail on SauceLabs to incorrect timezone  
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.ACTIVE, 
        null, 
        false, 
        false, 
        false, 
        1l));
    
    stubFor(get(urlEqualTo("/rest/events/12345"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
              .registerModule(new JodaModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .writeValueAsString(new Event(12345l, 
                "Open Event", "2", 
                getDate(2010, 1, 1), 
                getDate(2010, 1, 2), 
                null, 
                null, 
                null, 
                8l, 
                "", 
                "", 
                new ArrayList<String>(), 
                "", 
                null, 
                false, 
                null, 
                "Event for automatic testing (Open)", 
                "Test Admin", 
                "admin@foyt.fi", 
                "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
                null, 
                Event.Status.PENDING, 
                null, 
                false, 
                false, 
                false, 
                1l)))));

    stubFor(put(urlEqualTo("/rest/events/12345"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
            .registerModule(new JodaModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writeValueAsString(new Event(12345l, 
              "Open Event", "2", 
              getDate(2010, 1, 1), 
              getDate(2010, 1, 2), 
              null, 
              null, 
              null, 
              8l, 
              "", 
              "", 
              new ArrayList<String>(), 
              "", 
              null, 
              false, 
              null, 
              "Event for automatic testing (Open)", 
              "Test Admin", 
              "admin@foyt.fi", 
              "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
              null, 
              Event.Status.ACTIVE, 
              null, 
              false, 
              false, 
              false, 
              1l)))));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-event-settings-published");
    clickSelector(".illusion-event-settings-save");  
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, putRequestedFor(urlEqualTo("/rest/events/12345"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }

  @Test
  @SqlSets ({"basic-users", "illusion-basic", "illusion-event", "illusion-event-organizer", "illusion-larp-kalenteri"})
  public void testEventLarpKalenteriUpdateUnpublish() throws Exception {
    stubLarpKalenteriAccessToken();
    stubLarpKalenteriTypes();
    stubLarpKalenteriGenres();

    String requestBody = (new com.fasterxml.jackson.databind.ObjectMapper()
      .registerModule(new JodaModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .setSerializationInclusion(Include.NON_NULL))
      .writeValueAsString(new Event(12345l, 
        "Open Event", "2", 
        null, // getDate(2010, 1, 1), FIXME: These fail on SauceLabs to incorrect timezone  
        null, // getDate(2010, 1, 2), FIXME: These fail on SauceLabs to incorrect timezone  
        null, 
        null, 
        null, 
        8l, 
        "", 
        "", 
        new ArrayList<String>(), 
        "", 
        null, 
        false, 
        null, 
        "Event for automatic testing (Open)", 
        "Test Admin", 
        "admin@foyt.fi", 
        "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
        null, 
        Event.Status.PENDING, 
        null, 
        false, 
        false, 
        false, 
        1l));
    
    stubFor(get(urlEqualTo("/rest/events/12345"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
              .registerModule(new JodaModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .writeValueAsString(new Event(12345l, 
                "Open Event", "2", 
                getDate(2010, 1, 1), 
                getDate(2010, 1, 2), 
                null, 
                null, 
                null, 
                8l, 
                "", 
                "", 
                new ArrayList<String>(), 
                "", 
                null, 
                false, 
                null, 
                "Event for automatic testing (Open)", 
                "Test Admin", 
                "admin@foyt.fi", 
                "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
                null, 
                Event.Status.ACTIVE, 
                null, 
                false, 
                false, 
                false, 
                1l)))));

    stubFor(put(urlEqualTo("/rest/events/12345"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody((new com.fasterxml.jackson.databind.ObjectMapper())
            .registerModule(new JodaModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writeValueAsString(new Event(12345l, 
              "Open Event", "2", 
              getDate(2010, 1, 1), 
              getDate(2010, 1, 2), 
              null, 
              null, 
              null, 
              8l, 
              "", 
              "", 
              new ArrayList<String>(), 
              "", 
              null, 
              false, 
              null, 
              "Event for automatic testing (Open)", 
              "Test Admin", 
              "admin@foyt.fi", 
              "http://test.forgeandillusion.net:8080/illusion/event/openevent", 
              null, 
              Event.Status.PENDING, 
              null, 
              false, 
              false, 
              false, 
              1l)))));
    
    acceptCookieDirective(getWebDriver());
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/settings");
    
    clickSelector(".illusion-event-settings-published");
    clickSelector(".illusion-event-settings-save");  
    waitForPageLoad();
    
    navigate("/illusion/event/openevent/settings");
    
    verify(1, putRequestedFor(urlEqualTo("/rest/events/12345"))
      .withRequestBody(equalToJson(requestBody, JSONCompareMode.LENIENT))    
    );

    assertSelectorPresent(".illusion-create-event-larp-kalenteri:checked");
  }
}
