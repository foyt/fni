package fi.foyt.fni.auth;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.api.DropBoxApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@Dependent
public class DropboxAuthenticationStrategy extends OAuthAuthenticationStrategy {
  
	@Inject
	private SystemSettingsController systemSettingsController;

	@Override
	protected String getApiKey() {
		return systemSettingsController.getSetting(SystemSettingKey.DROPBOX_APIKEY);
	}

	@Override
	protected String getApiSecret() {
		return systemSettingsController.getSetting(SystemSettingKey.DROPBOX_APISECRET);
	}

	@Override
	protected String getCallbackUrl() {
		return systemSettingsController.getSetting(SystemSettingKey.DROPBOX_CALLBACKURL);
	}

	@Override
	protected String[] getRequiredScopes() {
		return null;
	}
  
  @Override
  public boolean getSupportLogin() {
    return false;
  }

  protected java.lang.Class<? extends org.scribe.builder.api.Api> getApiClass() {
    return DropBoxApi.class;
  };
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "oauth_token");
  }

  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      
      DropboxUserInfo userInfoObject = objectMapper.readValue(OAuthUtils.doGetRequest(service, accessToken, "https://api.dropbox.com/1/account/info").getBody(), DropboxUserInfo.class);
      String uid = userInfoObject.getUid();
      String displayName = userInfoObject.getDisplayName();
      String[] names = splitNames(displayName);
      String country = userInfoObject.getCountry();
      Locale userLocale = locale;
      String sourceId = displayName;
      
      if (StringUtils.isNotBlank(country)) {
        country = locale.getLanguage();
        userLocale = new Locale(country);
      }

      return loginUser(AuthSource.DROPBOX, sourceId, accessToken.getToken(), accessToken.getSecret(), null, uid, null, names[0], names[1], null, userLocale, null);
    } catch (IOException e) {
    	throw new ExternalLoginFailedException();
    }
  }
  
  private static String[] splitNames(String displayName) {
    int lastNameIndex = displayName.lastIndexOf(' ');
    String firstName = "";
    String lastName = "";
    
    if (lastNameIndex > -1) {
      firstName = displayName.substring(0, lastNameIndex);
      lastName = displayName.substring(lastNameIndex + 1);
    } else {
      firstName = displayName;
    }
    
    return new String[] {
      firstName,
      lastName
    };
  }
  
  @SuppressWarnings ("unused")
  @JsonIgnoreProperties (ignoreUnknown = true)
  private static class DropboxUserInfo {
    
    public String getUid() {
      return uid;
    }
    
    public void setUid(String uid) {
      this.uid = uid;
    }
    
    public String getCountry() {
      return country;
    }
    
    public void setCountry(String country) {
      this.country = country;
    }
    
    public String getDisplayName() {
      return displayName;
    }
    
    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }
    
    private String uid;
    
    private String country;
    
    @JsonProperty ("display_name")
    private String displayName;
  }
}