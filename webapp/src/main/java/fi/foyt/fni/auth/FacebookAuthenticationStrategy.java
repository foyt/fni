package fi.foyt.fni.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@Dependent
public class FacebookAuthenticationStrategy extends OAuthAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;

	@Override
	protected String getApiKey() {
		return systemSettingsController.getSetting(SystemSettingKey.FACEBOOK_APIKEY);
	}

	@Override
	protected String getApiSecret() {
		return systemSettingsController.getSetting(SystemSettingKey.FACEBOOK_APISECRET);
	}

	@Override
	protected String getCallbackUrl() {
		return systemSettingsController.getSetting(SystemSettingKey.FACEBOOK_CALLBACKURL);
	}

	@Override
	protected String[] getRequiredScopes() {
		return new String[] {
		  "email"
		};
	}
  
  @Override
  public boolean getSupportLogin() {
    return true;
  }

  protected java.lang.Class<? extends org.scribe.builder.api.Api> getApiClass() {
    return FacebookApi.class;
  };
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "code");
  }

  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String response = OAuthUtils.doGetRequest(service, accessToken, "https://graph.facebook.com/me").getBody();
      FacebookUser meObject = objectMapper.readValue(response, FacebookUser.class);
      Integer expiresIn = extractExpires(accessToken);
      Date expires = null;
      if (expiresIn != null) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, expiresIn);
        expires = calendar.getTime(); 
      }
      
      String id = meObject.getId();
      String firstName = meObject.getFirstName();
      String lastName = meObject.getLastName();
      String email = meObject.getEmail();
      String username = meObject.getUsername();
      Locale userLocale = meObject.getLocale();
      
      return loginUser(AuthSource.FACEBOOK, username, accessToken.getToken(), accessToken.getSecret(), expires, id, Arrays.asList(email), firstName, lastName, null, userLocale, grantedScopes);
    } catch (IOException e) {
    	throw new ExternalLoginFailedException();
    }
  };
  
  private Integer extractExpires(Token accessToken) {
    try {
      Pattern pattern = Pattern.compile("expires=[0-9]*");
      Matcher matcher = pattern.matcher(accessToken.getRawResponse());
      if (matcher.find()) {
        String[] split = matcher.group().split("=");
        if (split.length == 2) 
          return NumberUtils.createInteger(split[1]);
      }
      
      return null;
    } catch (Exception e) {
      return null;
    }
  }
  
  @JsonIgnoreProperties(ignoreUnknown = true)
  @SuppressWarnings ("unused")
  private static class FacebookUser {
    
    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getGender() {
      return gender;
    }

    public void setGender(String gender) {
      this.gender = gender;
    }

    public Locale getLocale() {
      return locale;
    }

    public void setLocale(Locale locale) {
      this.locale = locale;
    }
    
    public String getEmail() {
      return email;
    }
    
    public void setEmail(String email) {
      this.email = email;
    }

    private String id;
    
    private String name;
    
    @JsonProperty ("first_name")
    private String firstName;
    
    @JsonProperty ("last_name")
    private String lastName;
    
    private String link;
    
    private String username;
    
    private String gender;
    
    private Locale locale;
    
    private String email;
  } 

}