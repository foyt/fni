package fi.foyt.fni.view.users;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join ( path = "/logout", to = "/users/logout.jsf")
public class LogoutBackingBean {
	
	@Inject
  private SessionController sessionController;
	
	@RequestAction
	@Deferred
	public void load() throws IOException {
		sessionController.logout();
    
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect(new StringBuilder()
		  .append(externalContext.getRequestContextPath())
		  .append("/")
		  .toString());
	}
	
}
