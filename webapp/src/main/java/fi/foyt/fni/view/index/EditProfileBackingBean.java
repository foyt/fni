package fi.foyt.fni.view.index;

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
	
	@LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public void basicSave() {
		userController.updateFirstName(sessionController.getLoggedUser(), getBasicFirstName());
		userController.updateLastName(sessionController.getLoggedUser(), getBasicLastName());
		userController.updateNickname(sessionController.getLoggedUser(), getBasicNickname());
		
		FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.editProfile.basicSaved"));
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
	private long quotaUsed;
	private long quotaReserved;
}
