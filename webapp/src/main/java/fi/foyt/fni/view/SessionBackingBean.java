package fi.foyt.fni.view;

import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.LocaleUtils;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@Named
@RequestScoped
@Stateful
public class SessionBackingBean {

	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;
	
	public boolean isLoggedIn() {
		return sessionController.isLoggedIn();
	}
	
	public User getLoggedUser() {
		return sessionController.getLoggedUser();
	}
	
	public String getLoggedUserEmail() {
		User loggedUser = getLoggedUser();
		if (loggedUser != null) {
			return userController.getUserPrimaryEmail(loggedUser);
		}
		
		return null;
	}
	
	public Locale getLocale() {
		return sessionController.getLocale();
	}
	
	public void changeLanguage(String language) {
		sessionController.setLocale(LocaleUtils.toLocale(language));
	}
	
}
