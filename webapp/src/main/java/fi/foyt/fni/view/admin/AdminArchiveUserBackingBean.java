package fi.foyt.fni.view.admin;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "admin-archive-user", 
		pattern = "/admin/archive-user/#{adminArchiveUserBackingBean.userId}", 
		viewId = "/admin/archive-user.jsf"
  )
})
public class AdminArchiveUserBackingBean {
  
  @Inject
  private UserController userController;
  
	@URLAction
	@LoggedIn
	@Secure (Permission.SYSTEM_ADMINISTRATION)
	public String load() throws FileNotFoundException {
	  if (getUserId() == null) {
	    throw new FileNotFoundException();
	  }
	  
	  User user = userController.findUserById(getUserId());
	  if (user == null) {
      throw new FileNotFoundException();
	  }
	  
	  userController.archiveUser(user);
	  
	  return "/index.jsf";
	}
	
	public Long getUserId() {
    return userId;
  }
	
	public void setUserId(Long userId) {
    this.userId = userId;
  }
	
  private Long userId;
}
