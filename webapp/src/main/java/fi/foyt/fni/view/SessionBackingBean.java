package fi.foyt.fni.view;

import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.session.SessionController;

@Named
@RequestScoped
@Stateful
public class SessionBackingBean {

	@Inject
	private SessionController sessionController;
	
	public boolean isLoggedIn() {
		return sessionController.isLoggedIn();
	}
	
	public Locale getLocale() {
		return sessionController.getLocale();
	}
	
}
