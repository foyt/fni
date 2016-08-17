package fi.foyt.fni.test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import com.jayway.restassured.specification.RequestSpecification;

import fi.foyt.fni.test.AbstractTest;

public class AbstractRestTest extends AbstractTest {

  @Before
  public void setupRestAssured() {
    RestAssured.baseURI = getAppUrl() + "/rest";
    RestAssured.port = getPortHttps();

    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
      ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
        @Override
        public ObjectMapper create(@SuppressWarnings("rawtypes") Class cls, String charset) {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.registerModule(new JSR310Module());
          objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
          return objectMapper;
        }
      })
    );
    
    String trustStore = System.getProperty("javax.net.ssl.trustStore");
    String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    
    if (StringUtils.isNotBlank(trustStore) && StringUtils.isNotBlank(trustStorePassword)) {
      RestAssured.config.sslConfig(new SSLConfig().keystore(trustStore, trustStorePassword));
    }
  }

  protected RequestSpecification givenPlain(String accessToken) {
    return given().header("Authorization", "Bearer " + accessToken);
  }

  protected RequestSpecification givenJson() {
    return given().contentType("application/json");
  }

  protected RequestSpecification givenJson(String accessToken) {
    return given().header("Authorization", "Bearer " + accessToken).contentType("application/json");
  }

  protected String createServiceToken() throws OAuthSystemException, OAuthProblemException {
    return createServiceToken("client-id", "client-secret", getAppUrl(true) + "/fake-redirect");
  }

  protected String createServiceToken(String clientId, String clientSecet, String redirectURI) throws OAuthSystemException, OAuthProblemException {
    String tokenEndpoint = getAppUrl(true) + "/oauth2/token";

    OAuthClientRequest request = OAuthClientRequest.tokenLocation(tokenEndpoint).setGrantType(GrantType.CLIENT_CREDENTIALS).setClientId(clientId)
        .setClientSecret(clientSecet).setRedirectURI(redirectURI).buildQueryMessage();

    OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

    OAuthJSONAccessTokenResponse response = oAuthClient.accessToken(request);

    assertNotNull(response);
    assertNotNull(response.getAccessToken());

    return response.getAccessToken();
  }

  protected static Matcher<Instant> sameInstant(final Instant instant) {
    return new BaseMatcher<Instant>(){

      @Override
      public void describeTo(Description description) {
        description.appendText("same instant: ").appendValue(instant.toString());
      }

      @Override
      public boolean matches(Object item) {
        if (item == null && instant == null) {
          return true;
        }
        
        if (item instanceof String) {
          item = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse((String) item));
        }
        
        if (!(item instanceof Instant)) {
          return false;
        }
        
        if (item == null || instant == null) {
          return false;
        }
        
        return ((Instant) item).getEpochSecond() == instant.getEpochSecond();
      }
      
    };
  }
}
