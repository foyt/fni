package fi.foyt.fni.view.users;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.users.UserVerificationKey;
import fi.foyt.fni.utils.faces.FacesUtils;

@Named
@RequestScoped
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "users-verification", 
		pattern = "/users/verify/#{verifyBackingBean.key}", 
		viewId = "/users/verify.jsf"
  )
})
public class VerifyBackingBean {

	@Inject
	private AuthenticationController authenticationController;
	
	@URLAction
	public void load() throws IOException {
		UserVerificationKey verificationKey = authenticationController.findVerificationKey(getKey());
		if (verificationKey == null) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("users.verification.invalidVerificationKey"));
		} else {
			InternalAuth internalAuth = authenticationController.findInternalAuthByUser(verificationKey.getUser());
			if (internalAuth != null) {
  			authenticationController.verifyInternalAuth(verificationKey, internalAuth);
    		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    		externalContext.redirect(new StringBuilder()
      	  .append(externalContext.getRequestContextPath())
      	  .append("/")
      	  .toString());   
			} else {
				FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("users.verification.internalAuthNotFound"));
			}
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	private String key;
}
