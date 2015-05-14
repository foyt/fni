package fi.foyt.fni.test.ui.base;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.openqa.selenium.remote.RemoteWebDriver;

public class AbstractIllusionUITest extends AbstractUITest {

  private static final String CUSTOM_EVENT_HOST = "custom-test.forgeandillusion.net";
  
  protected String getCustomEventUrl() {
    return "http://" + CUSTOM_EVENT_HOST + ':' + getPortHttp() + '/' + getCtxPath();
  }

  protected void loginCustomEvent(String email, String password) {
    RemoteWebDriver driver = getWebDriver();
    
    if (!driver.getCurrentUrl().matches(".*/login.*")) {
      findElementBySelector(".menu-tools-login").click();
    }
    
    waitForSelectorVisible(".user-login-email");
    typeSelectorInputValue(".user-login-email", email);
    typeSelectorInputValue(".user-login-password", password);
    clickSelector(".user-login-button");
    waitForUrlNotMatches(".*/login.*");
    
    assertSelectorPresent(".menu-tools-account");
    assertSelectorNotPresent(".menu-tools-login");
  }

  protected void assertMenuItems() {
    assertSelectorVisible(String.format("a[href='%s/']", getAppUrl()));
    assertSelectorVisible(String.format("a[href='%s/forge']", getAppUrl()));
    assertSelectorVisible(String.format("a[href='%s/illusion']", getAppUrl()));
    assertSelectorVisible(String.format("a[href='%s/gamelibrary']", getAppUrl()));
    assertSelectorVisible(String.format("a[href='%s/forum']", getAppUrl()));
    
    assertSelectorTextIgnoreCase(String.format("a[href='%s/forge']", getAppUrl()), "Forge");
    assertSelectorTextIgnoreCase(String.format("a[href='%s/illusion']", getAppUrl()), "Illusion");
    assertSelectorTextIgnoreCase(String.format("a[href='%s/gamelibrary']", getAppUrl()), "Game Library");
    assertSelectorTextIgnoreCase(String.format("a[href='%s/forum']", getAppUrl()), "Forum");
  }
  
  protected void deleteIllusionTemplate(String eventUrlName, String templateName) throws Exception {
    executeSql("delete from IllusionEventTemplate where event_id = (select id from IllusionEvent where urlName = ?) and name = ?", eventUrlName, templateName);
  }

  protected void stubLarpKalenteriGenres() {
    stubFor(get(urlEqualTo("/rest/genres"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("[{\"id\":\"fantasy\",\"name\":{\"fi\":\"Fantasia\",\"en\":\"Fantasy\"}},{\"id\":\"sci-fi\",\"name\":{\"fi\":\"Sci-fi\",\"en\":\"Sci-fi\"}},{\"id\":\"scifi\",\"name\":{\"fi\":\"Sci-fi\",\"en\":\"Sci-fi\"}},{\"id\":\"cyberpunk\",\"name\":{\"fi\":\"Cyberpunk\",\"en\":\"Cyberpunk\"}},{\"id\":\"steampunk\",\"name\":{\"fi\":\"Steampunk\",\"en\":\"Steampunk\"}},{\"id\":\"post-apocalyptic\",\"name\":{\"fi\":\"Post-apokalyptinen\",\"en\":\"Post-apocalyptic\"}},{\"id\":\"postapo\",\"name\":{\"fi\":\"Post-apokalyptinen\",\"en\":\"Post-apocalyptic\"}},{\"id\":\"historical\",\"name\":{\"fi\":\"Historiallinen\",\"en\":\"Historical\"}},{\"id\":\"thriller\",\"name\":{\"fi\":\"J\u00e4nnitys\",\"en\":\"Thriller\"}},{\"id\":\"horror\",\"name\":{\"fi\":\"Kauhu\",\"en\":\"Horror\"}},{\"id\":\"reality\",\"name\":{\"fi\":\"Realismi\",\"en\":\"Reality\"}},{\"id\":\"city larp\",\"name\":{\"fi\":\"Kaupunkipeli\",\"en\":\"City larp\"}},{\"id\":\"city\",\"name\":{\"fi\":\"Kaupunkipeli\",\"en\":\"City larp\"}},{\"id\":\"new weird\",\"name\":{\"fi\":\"Uuskumma\",\"en\":\"New weird\"}},{\"id\":\"newweird\",\"name\":{\"fi\":\"Uuskumma\",\"en\":\"New weird\"}},{\"id\":\"action\",\"name\":{\"fi\":\"Toiminta\",\"en\":\"Action\"}},{\"id\":\"drama\",\"name\":{\"fi\":\"Draama\",\"en\":\"Drama\"}},{\"id\":\"humor\",\"name\":{\"fi\":\"Huumori\",\"en\":\"Humor\"}}]")));
  }

  protected void stubLarpKalenteriTypes() {
    stubFor(get(urlEqualTo("/rest/types"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("[{\"id\":\"2\",\"name\":{\"fi\":\"Larpit\",\"en\":\"Larps\"}},{\"id\":\"3\",\"name\":{\"fi\":\"Conit ja miitit\",\"en\":\"Conventions and meetups\"}},{\"id\":\"4\",\"name\":{\"fi\":\"Kurssit ja ty\u00f6pajat\",\"en\":\"Courses and workshops\"}},{\"id\":\"5\",\"name\":{\"fi\":\"Muut\",\"en\":\"Other\"}}]")));
  }

  protected void stubLarpKalenteriAccessToken() {
    stubFor(post(urlEqualTo("/oauth2/token"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{\"expires_in\":3600,\"access_token\":\"accesstoken\"}")));
  }
}
