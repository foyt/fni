package fi.foyt.fni.test.rest;

import static org.junit.Assert.assertNotNull;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({ @DefineSqlSet(id = "service-client", before = "rest-service-client-setup.sql", after = "rest-service-client-teardown.sql") })
public class ResourceOwnerPasswordCredentialsTestIT extends AbstractRestTest {

  @Test
  @SqlSets("service-client")
  public void testServiceAccountToken() throws OAuthSystemException, OAuthProblemException {
    String tokenEndpoint = getAppUrl() + "/oauth2/token";

    OAuthClientRequest request = OAuthClientRequest.tokenLocation(tokenEndpoint)
        .setGrantType(GrantType.CLIENT_CREDENTIALS)
        .setClientId("client-id")
        .setClientSecret("client-secret")
        .setRedirectURI(getAppUrl() + "/fake-redirect")
        .buildQueryMessage();
    
    OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());    

    OAuthJSONAccessTokenResponse response = oAuthClient.accessToken(request);
    assertNotNull(response);
    assertNotNull(response.getAccessToken());
  }

}
