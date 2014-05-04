package fi.foyt.fni.view.users;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.users.UserVerificationKey;
import fi.foyt.fni.utils.faces.FacesUtils;

@Named
@RequestScoped
@Stateful
@Join (path = "/users/verify/{key}", to = "/users/verify.jsf")
public class VerifyBackingBean {
  
  @Parameter
  private String key;
  
	@Inject
	private AuthenticationController authenticationController;
	
	@RequestAction
	@Deferred
	public void load() throws IOException {
		UserVerificationKey verificationKey = authenticationController.findVerificationKey(getKey());
		if (verificationKey == null) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("users.verification.invalidVerificationKey"));
		} else {
		  FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.verification.userVerifiedMessage"));
		  
			InternalAuth internalAuth = authenticationController.findInternalAuthByUser(verificationKey.getUser());
			if (internalAuth != null) {
  			authenticationController.verifyInternalAuth(verificationKey, internalAuth);
    		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    		externalContext.redirect(new StringBuilder()
      	  .append(externalContext.getRequestContextPath())
      	  .append("/login/")
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
}
