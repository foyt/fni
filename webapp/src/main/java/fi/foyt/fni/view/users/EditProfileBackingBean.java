package fi.foyt.fni.view.users;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Stateful
@Named
@Join (path = "/editprofile", to = "/users/editprofile.jsf")
@LoggedIn
@Secure (Permission.PROFILE_UPDATE)
public class EditProfileBackingBean {
  
  @Parameter
  private String redirectUrl;

	@Inject
	private UserController userController;

	@Inject
	private SessionController sessionController;
	
	@Inject
	private AuthenticationController authenticationController;
	
	@RequestAction 
	@Deferred
	public String load() throws IOException {
		User loggedUser = sessionController.getLoggedUser();
		firstName = loggedUser.getFirstName();
		lastName = loggedUser.getLastName();
  	nickname = loggedUser.getNickname();
  	about = loggedUser.getAbout();
  	contactInfoFieldHomePage = userController.getContactFieldValue(loggedUser, UserContactFieldType.HOME_PAGE);
  	contactInfoFieldBlog = userController.getContactFieldValue(loggedUser, UserContactFieldType.BLOG);
  	contactInfoFieldFacebook = userController.getContactFieldValue(loggedUser, UserContactFieldType.FACEBOOK);
  	contactInfoFieldTwitter = userController.getContactFieldValue(loggedUser, UserContactFieldType.TWITTER);
  	contactInfoFieldLinkedIn = userController.getContactFieldValue(loggedUser, UserContactFieldType.LINKEDIN);
  	contactInfoFieldGooglePlus = userController.getContactFieldValue(loggedUser, UserContactFieldType.GOOGLE_PLUS);
  	
  	profileImageSource = loggedUser.getProfileImageSource();
		userIdentifiers = authenticationController.listUserIdentifiers(loggedUser);
		hasFniProfileImage = userController.hasProfileImage(loggedUser);
		
		addAuthenticationSourcesSelectItems = new ArrayList<>();
		
		boolean hasGoogleAuthSource = false;
		boolean hasYahooAuthSource = false;
		boolean hasFacebookAuthSource = false;
		boolean hasInternalAuthSource = false;
		
		for (UserIdentifier userIdentifier : userIdentifiers) {
			switch (userIdentifier.getAuthSource()) {
				case GOOGLE:
					hasGoogleAuthSource = true;
        break;
				case FACEBOOK:
					hasFacebookAuthSource = true;
				break;
				case YAHOO:
					hasYahooAuthSource = true;
				break;
				case INTERNAL:
					hasInternalAuthSource = true;
				break;
				default:
			  break;
			}
		}
		
		if (!hasGoogleAuthSource) {
			addAuthenticationSourcesSelectItems.add(new SelectItem(AuthSource.GOOGLE, FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceGoogle")));
		}

		if (!hasFacebookAuthSource) {
			addAuthenticationSourcesSelectItems.add(new SelectItem(AuthSource.FACEBOOK, FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceFacebook")));
		}

		if (!hasYahooAuthSource) {
			addAuthenticationSourcesSelectItems.add(new SelectItem(AuthSource.YAHOO, FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceYahoo")));
		}

		if (!hasInternalAuthSource) {
			addAuthenticationSourcesSelectItems.add(new SelectItem(AuthSource.INTERNAL, FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceForgeAndIllusion")));
		}
		
		return null;
	}
	
	@RequestAction 
	@IgnorePostback
  @Deferred
  public void checkMissing() {
	  User loggedUser = sessionController.getLoggedUser();
	  
    if (StringUtils.isBlank(loggedUser.getFirstName()) || StringUtils.isBlank(loggedUser.getLastName())) {
      FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.editProfile.requiredFieldsMissing"));
    }
	}
	
	public String getRedirectUrl() {
    return redirectUrl;
  }
	
	public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
	
	public UserProfileImageSource getProfileImageSource() {
		return profileImageSource;
	}
	
	public void setProfileImageSource(UserProfileImageSource profileImageSource) {
		this.profileImageSource = profileImageSource;
	}

	public Boolean getHasFniProfileImage() {
		return hasFniProfileImage;
	}
	
	public List<UserIdentifier> getUserIdentifiers() {
		return userIdentifiers;
	}
	
	public String getAuthenticationSourceName(AuthSource authSource) {
		switch (authSource) {
			case DROPBOX:
				return FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceDropbox");
			case FACEBOOK:
				return FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceFacebook");
			case GOOGLE:
				return FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceGoogle");
			case INTERNAL:
				return FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceForgeAndIllusion");
			case YAHOO:
				return FacesUtils.getLocalizedValue("users.editProfile.authenticationSourceYahoo");
			default:
			break;
		}
		
		return null;
	}
	
	public String getNewInternalAuthencationSourcePassword1() {
		return newInternalAuthencationSourcePassword1;
	}
	
	public void setNewInternalAuthencationSourcePassword1(String newInternalAuthencationSourcePassword1) {
		this.newInternalAuthencationSourcePassword1 = newInternalAuthencationSourcePassword1;
	}
	
	public String getNewInternalAuthencationSourcePassword2() {
		return newInternalAuthencationSourcePassword2;
	}
	
	public void setNewInternalAuthencationSourcePassword2(String newInternalAuthencationSourcePassword2) {
		this.newInternalAuthencationSourcePassword2 = newInternalAuthencationSourcePassword2;
	}
	
	public String getChangePassword1() {
		return changePassword1;
	}
	public void setChangePassword1(String changePassword1) {
		this.changePassword1 = changePassword1;
	}
	
	public String getChangePassword2() {
		return changePassword2;
	}
	
	public void setChangePassword2(String changePassword2) {
		this.changePassword2 = changePassword2;
	}

	public String getContactInfoFieldHomePage() {
		return contactInfoFieldHomePage;
	}

	public void setContactInfoFieldHomePage(String contactInfoFieldHomePage) {
		this.contactInfoFieldHomePage = contactInfoFieldHomePage;
	}

	public String getContactInfoFieldBlog() {
		return contactInfoFieldBlog;
	}

	public void setContactInfoFieldBlog(String contactInfoFieldBlog) {
		this.contactInfoFieldBlog = contactInfoFieldBlog;
	}

	public String getContactInfoFieldFacebook() {
		return contactInfoFieldFacebook;
	}

	public void setContactInfoFieldFacebook(String contactInfoFieldFacebook) {
		this.contactInfoFieldFacebook = contactInfoFieldFacebook;
	}

	public String getContactInfoFieldTwitter() {
		return contactInfoFieldTwitter;
	}

	public void setContactInfoFieldTwitter(String contactInfoFieldTwitter) {
		this.contactInfoFieldTwitter = contactInfoFieldTwitter;
	}

	public String getContactInfoFieldLinkedIn() {
		return contactInfoFieldLinkedIn;
	}

	public void setContactInfoFieldLinkedIn(String contactInfoFieldLinkedIn) {
		this.contactInfoFieldLinkedIn = contactInfoFieldLinkedIn;
	}

	public String getContactInfoFieldGooglePlus() {
		return contactInfoFieldGooglePlus;
	}

	public void setContactInfoFieldGooglePlus(String contactInfoFieldGooglePlus) {
		this.contactInfoFieldGooglePlus = contactInfoFieldGooglePlus;
	}
	
	public List<SelectItem> getAddAuthenticationSourcesSelectItems() {
		return addAuthenticationSourcesSelectItems;
	}
	
	public void enableFacebookAuthSource() throws IOException {
		enableAuthSource(AuthSource.FACEBOOK);
	}
	
	public void enableGoogleAuthSource() throws IOException {
		enableAuthSource(AuthSource.GOOGLE);
	}
	
	public void enableYahooAuthSource() throws IOException {
		enableAuthSource(AuthSource.YAHOO);
	}
	
	private void enableAuthSource(AuthSource authSource) throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String contextPath = externalContext.getRequestContextPath();

		switch (authSource) {
			case FACEBOOK:
					externalContext.redirect(new StringBuilder()
					  .append(contextPath)
					  .append("/login?loginMethod=FACEBOOK&redirectUrl=")
					  .append(URLEncoder.encode(contextPath + "/editprofile", "UTF-8"))
					  .toString());
			  return;
			case GOOGLE:
				externalContext.redirect(new StringBuilder()
	    	  .append(contextPath)
	  	    .append("/login?loginMethod=GOOGLE&redirectUrl=")
	  	    .append(URLEncoder.encode(contextPath + "/editprofile", "UTF-8"))
	  	    .toString());
			  return;
			case YAHOO:
				externalContext.redirect(new StringBuilder()
	    	  .append(contextPath)
	  	    .append("/login?loginMethod=YAHOO&redirectUrl=")
	  	    .append(URLEncoder.encode(contextPath + "/editprofile", "UTF-8"))
	  	    .toString());
			  return;
			default:
				break;
		}
	}
	
	public String addNewInternalAuthencationSource() {
		if (StringUtils.isBlank(getNewInternalAuthencationSourcePassword1())||DigestUtils.md5Hex("").equals(getNewInternalAuthencationSourcePassword1())) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.editProfile.addInternalAuthenticationSourcePasswordRequired"));
		} else {
  		if (!StringUtils.equals(getNewInternalAuthencationSourcePassword1(), getNewInternalAuthencationSourcePassword2())) {
  			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.editProfile.addInternalAuthenticationSourcePasswordsDoNotMatch"));
  		} else {
  			InternalAuth internalAuth = authenticationController.createInternalAuth(sessionController.getLoggedUser(), getNewInternalAuthencationSourcePassword1());
  			authenticationController.verifyInternalAuth(internalAuth);
  		}
		}
		
		return "pretty:";
	}
	
	public void removeAuthenticationSource(Long userIdentifierId) {
		UserIdentifier userIdentifier = authenticationController.findUserIdentifierById(userIdentifierId);
		if (userIdentifier != null) {
		  authenticationController.removeUserIdentifier(userIdentifier);
		}
	}

	public void save() throws IOException {
		User loggedUser = sessionController.getLoggedUser();
		
		userController.updateFirstName(loggedUser, getFirstName());
		userController.updateLastName(loggedUser, getLastName());
		userController.updateNickname(loggedUser, getNickname());
		userController.updateAbout(loggedUser, getAbout());
		userController.updateProfileImageSource(loggedUser, getProfileImageSource());
  	userController.setContactFieldValue(loggedUser, UserContactFieldType.HOME_PAGE, getContactInfoFieldHomePage());
  	userController.setContactFieldValue(loggedUser, UserContactFieldType.BLOG, getContactInfoFieldBlog());
  	userController.setContactFieldValue(loggedUser, UserContactFieldType.FACEBOOK, getContactInfoFieldFacebook());
  	userController.setContactFieldValue(loggedUser, UserContactFieldType.TWITTER, getContactInfoFieldTwitter());
  	userController.setContactFieldValue(loggedUser, UserContactFieldType.LINKEDIN, getContactInfoFieldLinkedIn());
  	userController.setContactFieldValue(loggedUser, UserContactFieldType.GOOGLE_PLUS, getContactInfoFieldGooglePlus());
  	
    FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.editProfile.savedMessage"));
    
    if (StringUtils.isNotBlank(getRedirectUrl())) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      ExternalContext externalContext = facesContext.getExternalContext();
      externalContext.redirect(getRedirectUrl());
    }
	}
	
	public void changePassword() {
		if (StringUtils.isBlank(getChangePassword1()) || DigestUtils.md5Hex("").equals(getChangePassword1())) {
      FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.editProfile.changePasswordPasswordRequired"));
		} else {
  		if (!StringUtils.equals(getChangePassword1(), getChangePassword2())) {
  			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.editProfile.changePasswordPasswordsDoNotMatch"));
  		} else {
  			authenticationController.setUserPassword(sessionController.getLoggedUser(), getChangePassword1());
  			FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.editProfile.changePasswordSuccess"));
  		}
		}
	}
	
	private String firstName;
	private String lastName;
	private String nickname;
	private String about;
	private Boolean hasFniProfileImage;
  private UserProfileImageSource profileImageSource;
  private List<UserIdentifier> userIdentifiers;
  private List<SelectItem> addAuthenticationSourcesSelectItems;
  private String newInternalAuthencationSourcePassword1;
  private String newInternalAuthencationSourcePassword2;
  private String changePassword1;
  private String changePassword2;
  private String contactInfoFieldHomePage;
  private String contactInfoFieldBlog;
  private String contactInfoFieldFacebook;
  private String contactInfoFieldTwitter;
  private String contactInfoFieldLinkedIn;
  private String contactInfoFieldGooglePlus;
}
