package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.net.URLEncoder;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.scribe.model.Token;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.persistence.model.materials.UbuntuOneRootFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.ubuntuone.UbuntuOneController;
import fi.foyt.fni.ubuntuone.UbuntuOneManager;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "forge-connect-ubuntu-one", 
		pattern = "/forge/connect-ubuntu-one", 
		viewId = "/forge/connect-ubuntu-one.jsf"
  )
})
public class ForgeConnectUbuntuOneBackingBean {

  @Inject
  private SessionController sessionController;

  @Inject
  private UbuntuOneManager ubuntuOneManager;

  @Inject
  private UbuntuOneController ubuntuOneController;

	@URLAction
	@LoggedIn
	public String load() throws IOException {
		// TODO: Proper error handling
		
    User loggedUser = sessionController.getLoggedUser();
    Token ubuntuOneToken = ubuntuOneManager.getUbuntuOneToken(loggedUser);
  	String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    
    if (ubuntuOneToken == null) {
    	// We are not authenticated with Ubuntu One, redirecting to authentication
    	String redirectUrl = contextPath + "/forge/connect-ubuntu-one";
    	
    	FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
    	  .append(contextPath)
  	    .append("/login?loginMethod=UBUNTU_ONE&redirectUrl=")
  	    .append(URLEncoder.encode(redirectUrl, "UTF-8"))
  	    .toString());
    } else {
      UbuntuOneRootFolder ubuntuOneRootFolder = ubuntuOneController.findUbuntuOneRootFolderByUser(loggedUser);
      if (ubuntuOneRootFolder == null) {
        String title = FacesUtils.getLocalizedValue("forge.ubuntuOneConnect.rootFolderName");
        ubuntuOneRootFolder = ubuntuOneController.createUbuntuOneRootFolder(loggedUser, title);
        FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("forge.ubuntuOneConnect.connectedMessage"));
      }
      
      return "pretty:forge-index";
    }
    
    return null;
	}

}
