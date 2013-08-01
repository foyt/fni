package fi.foyt.fni.view.auth;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "auth-logout", 
		pattern = "/logout", 
		viewId = "/auth/logout.jsf"
  )
})
public class LogoutBackingBean {
	
	@Inject
  private SessionController sessionController;
	
	@URLAction
	public void load() throws IOException {
		sessionController.logout();
    
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect(new StringBuilder()
		  .append(externalContext.getRequestContextPath())
		  .append("/")
		  .toString());
	}
	
}
