package fi.foyt.fni.view.users;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "users-edit-profile", 
		pattern = "/editprofile", 
		viewId = "/users/editprofile.jsf"
  )
})
public class EditProfileBackingBean {
	
	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;
	
	@Inject
	private MaterialController materialController;
	
	@PostConstruct
	public void init() {
		if (!sessionController.isLoggedIn()) {
			throw new UnauthorizedException();
		}
		
		User loggedUser = sessionController.getLoggedUser();
		basicFirstName = loggedUser.getFirstName();
		basicLastName = loggedUser.getLastName();
		basicNickname = loggedUser.getNickname();
		basicAbout = loggedUser.getAbout();

		contactInfoFieldHomePage = userController.getContactFieldValue(loggedUser, UserContactFieldType.HOME_PAGE);
		contactInfoFieldBlog = userController.getContactFieldValue(loggedUser, UserContactFieldType.BLOG);
		contactInfoFieldFacebook = userController.getContactFieldValue(loggedUser, UserContactFieldType.FACEBOOK);
		contactInfoFieldTwitter = userController.getContactFieldValue(loggedUser, UserContactFieldType.TWITTER);
		contactInfoFieldLinkedIn = userController.getContactFieldValue(loggedUser, UserContactFieldType.LINKEDIN);
		contactInfoFieldGooglePlus = userController.getContactFieldValue(loggedUser, UserContactFieldType.GOOGLE_PLUS);
		
		quotaUsed = materialController.getUserMaterialsTotalSize(loggedUser);
		quotaReserved = materialController.getUserQuota();
	}
	
	public String getBasicFirstName() {
		return basicFirstName;
	}
	
	public void setBasicFirstName(String basicFirstName) {
		this.basicFirstName = basicFirstName;
	}
	
	public String getBasicLastName() {
		return basicLastName;
	}
	
	public void setBasicLastName(String basicLastName) {
		this.basicLastName = basicLastName;
	}
	
	public String getBasicNickname() {
		return basicNickname;
	}
	
	public void setBasicNickname(String basicNickname) {
		this.basicNickname = basicNickname;
	}
	
	public String getBasicAbout() {
		return basicAbout;
	}
	
	public void setBasicAbout(String basicAbout) {
		this.basicAbout = basicAbout;
	}
	
	@LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public void basicSave() {
		userController.updateFirstName(sessionController.getLoggedUser(), getBasicFirstName());
		userController.updateLastName(sessionController.getLoggedUser(), getBasicLastName());
		userController.updateNickname(sessionController.getLoggedUser(), getBasicNickname());
		userController.updateAbout(sessionController.getLoggedUser(), getBasicAbout());
		
		FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.editProfile.basicSaved"));
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

	@LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public void contactInfoSave() {
		User loggedUser = sessionController.getLoggedUser();
		
		userController.setContactFieldValue(loggedUser, UserContactFieldType.HOME_PAGE, getContactInfoFieldHomePage());
		userController.setContactFieldValue(loggedUser, UserContactFieldType.BLOG, getContactInfoFieldBlog());
		userController.setContactFieldValue(loggedUser, UserContactFieldType.FACEBOOK, getContactInfoFieldFacebook());
		userController.setContactFieldValue(loggedUser, UserContactFieldType.TWITTER, getContactInfoFieldTwitter());
		userController.setContactFieldValue(loggedUser, UserContactFieldType.LINKEDIN, getContactInfoFieldLinkedIn());
		userController.setContactFieldValue(loggedUser, UserContactFieldType.GOOGLE_PLUS, getContactInfoFieldGooglePlus());

		FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.editProfile.contactInfoSaved"));
	}
	
	public String getQuotaUsage() {
		return FileUtils.byteCountToDisplaySize(quotaUsed);
	}

	public String getQuotaReserved() {
		return FileUtils.byteCountToDisplaySize(quotaReserved);
	}

	public String getQuotaPercent() {
		double quotaPercent = 0;
		if (quotaUsed > 0) {
		  quotaPercent = quotaUsed;
		  quotaPercent /= quotaReserved;
		  quotaPercent *= 100;
		}

		return String.format("%.2f", quotaPercent);
	}
	
	public boolean getQuotaExceeded() {
		return quotaUsed > quotaReserved;
	}
	
	private String basicFirstName;
	private String basicLastName;
	private String basicNickname;
	private String basicAbout;
	private String contactInfoFieldHomePage;
	private String contactInfoFieldBlog;
	private String contactInfoFieldFacebook;
	private String contactInfoFieldTwitter;
	private String contactInfoFieldLinkedIn;
	private String contactInfoFieldGooglePlus;
	private long quotaUsed;
	private long quotaReserved;	
}
