package fi.foyt.fni.view.users;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

@RequestScoped
@Named
@Stateful
@Join ( path = "/logout", to = "/users/logout.jsf")
public class LogoutBackingBean {
	
	@RequestAction
	@Deferred
	public String load() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "/index.jsf?faces-redirect=true";
	}
	
}
