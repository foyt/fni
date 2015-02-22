package fi.foyt.fni.view.admin;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
@Join (path = "/admin/archive-user/{userId}", to = "/admin/archive-user.jsf")
@LoggedIn
@Secure (Permission.SYSTEM_ADMINISTRATION)
public class AdminArchiveUserBackingBean {
  
  @Parameter
  @Matches ("[0-9]{1,}")
  private Long userId;
  
  @Inject
  private UserController userController;

  @Inject
  private NavigationController navigationController;
  
	@RequestAction
	public String load() throws FileNotFoundException {
	  if (getUserId() == null) {
      return navigationController.notFound();
	  }
	  
	  User user = userController.findUserById(getUserId());
	  if (user == null) {
      return navigationController.notFound();
	  }
	  
	  userController.archiveUser(user);
	  
	  return "/index.jsf?faces-redirect=true";
	}
	
	public Long getUserId() {
    return userId;
  }
	
	public void setUserId(Long userId) {
    this.userId = userId;
  }
}
