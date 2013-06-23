package fi.foyt.fni.utils.auth;

import java.util.Map;
import java.util.Set;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class OAuthUtils {

  public static Response doGetRequest(OAuthService service, Token accessToken, String url) {
    OAuthRequest request = new OAuthRequest(Verb.GET, url);
    service.signRequest(accessToken, request);
    return request.send();
  }

  public static Response doPostRequest(OAuthService service, Token accessToken, String url) {
    return doPostRequest(service, accessToken, url, null);
  }
  
  public static Response doPostRequest(OAuthService service, Token accessToken, String url, Map<String, String> parameters) {
    OAuthRequest request = new OAuthRequest(Verb.POST, url);

    if (parameters != null) {
      Set<String> keys = parameters.keySet();
      for (String key : keys) {
        request.addBodyParameter(key, parameters.get(key));
      }
    }
    
    service.signRequest(accessToken, request);

    return request.send();
  }

}
