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

import fi.foyt.fni.dropbox.DropboxController;
import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "forge-connect-dropbox", 
		pattern = "/forge/connect-dropbox", 
		viewId = "/forge/connect-dropbox.jsf"
  )
})
public class ForgeConnectDropboxBackingBean {

  @Inject
  private SessionController sessionController;

  @Inject
  private DropboxManager dropboxManager;

  @Inject
  private DropboxController dropboxController;

	@URLAction
	@LoggedIn
	public String load() throws IOException {
		// TODO: Proper error handling
		
    User loggedUser = sessionController.getLoggedUser();
    Token dropboxToken = dropboxManager.getDropboxToken(loggedUser);
  	String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    
    if (dropboxToken == null) {
    	// We are not authenticated with Dropbox, redirecting to authentication
    	String redirectUrl = contextPath + "/forge/connect-dropbox";
    	
    	FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
    	  .append(contextPath)
  	    .append("/login?loginMethod=DROPBOX&redirectUrl=")
  	    .append(URLEncoder.encode(redirectUrl, "UTF-8"))
  	    .toString());
    } else {
      DropboxRootFolder dropboxRootFolder = dropboxController.findDropboxRootFolderByUser(loggedUser);
      if (dropboxRootFolder == null) {
        String title = FacesUtils.getLocalizedValue("forge.dropboxConnect.rootFolderName");
        dropboxRootFolder = dropboxController.createDropboxRootFolder(loggedUser, title);
        FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("forge.dropboxConnect.connectedMessage"));
      }
      
      return "pretty:forge-index";
    }
    
    return null;
	}

}
