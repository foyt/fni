package fi.foyt.fni.view.index;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "user-profile", 
		pattern = "/users/#{userProfileBackingBean.userId}", 
		viewId = "/userprofile.jsf"
  )
})
public class UserProfileBackingBean {
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	private Long userId;
}
