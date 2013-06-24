package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class ImportGoogleDocumentsDialogViewController extends AbstractViewController {
	
	private final static String REQUIRED_SCOPE = "https://www.googleapis.com/auth/drive";

	@Inject
	private Logger logger;

	@Inject
	private MaterialController materialController;
	
	@Inject
	private SessionController sessionController;

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private DriveManager driveManager;
	
	@Override
	public boolean checkPermissions(ViewControllerContext context) {
		return sessionController.isLoggedIn();
	}

	@Override
	public void execute(ViewControllerContext context) {
		UserToken userToken = sessionController.getLoggedUserToken();
    Locale locale = context.getRequest().getLocale();

    if (AuthUtils.getInstance().isExpired(userToken) || !AuthUtils.getInstance().isGrantedScope(userToken, REQUIRED_SCOPE)) {
      try {
        String redirectUrl = URLEncoder.encode(context.getBasePath() + "/forge/?a=importgoogledocuments", "UTF-8");
        String loginUrl = context.getBasePath() + "/login/?loginMethod=GOOGLE&redirectUrl=" + redirectUrl + "&extraScopes=" + REQUIRED_SCOPE;
        String warningMessage = StringEscapeUtils.escapeJava(Locales.getText(locale, "forge.importGoogleDocuments.authenticationWarning"));
        context.getRequest().setAttribute("action", "authenticate");
        context.getRequest().setAttribute("authenticationUrl", loginUrl);
        context.getRequest().setAttribute("warningMessage", warningMessage);
        context.setIncludeJSP("/jsp/forge/importgoogledocumentsdialog.jsp");
      } catch (UnsupportedEncodingException e) {
        throw new ViewControllerException(Locales.getText(locale, "generic.error.configurationError"), e);
      }
    } else {
      User loggedUser = userToken.getUserIdentifier().getUser();
      Folder parentFolder = null;
    	Drive drive = driveManager.getUserDrive(userToken.getToken());
    	
    	if (context.getStringParameter("import") != null) {
    		String accountUser = System.getProperty("fni-google-drive.accountUser");
    		
    		Set<String> entryIds = context.getStringParameters("entryIds");
        for (String entryId : entryIds) {
        	if (materialController.findGoogleDocumentByCreatorAndDocumentId(loggedUser, entryId) == null) {
        		try {
							File file = driveManager.getFile(drive, entryId);

							if (!driveManager.hasRoles(drive, accountUser, entryId, "owner","reader", "writer")) {
								Permission permission = new Permission();
								permission.setRole("reader");
								permission.setType("user");
								permission.setValue(accountUser);
								driveManager.insertPermission(drive, file.getId(), permission);
								materialController.createGoogleDocument(loggedUser, null, parentFolder, file.getTitle(), file.getId(), file.getMimeType(), MaterialPublicity.PRIVATE);
							}
							
						} catch (IOException e) {
							logger.log(Level.SEVERE, "Communication with Google Drive failed", e);
		          throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleCommunicationError"), e);
						}
        	}
        }
        
        context.getRequest().setAttribute("action", "close");
    	} else if (context.getStringParameter("cancel") != null) {
        context.getRequest().setAttribute("action", "close");        
      } else {
      	try {
	        String folderId = context.getStringParameter("folderId");
	        if (folderId == null) {
	          context.getRequest().setAttribute("rootFolder", true);
  					FileList fileList = driveManager.listFiles(drive, "trashed != true and 'root' in parents");
  					List<File> files = fileList.getItems();
  	        context.getRequest().setAttribute("files", files);
          } else {
            context.getRequest().setAttribute("rootFolder", false);
  					FileList fileList = driveManager.listFiles(drive, "trashed != true and '" + folderId + "' in parents");
  					List<File> files = fileList.getItems();
  	        context.getRequest().setAttribute("files", files);
	        } 
	        
	        context.getRequest().setAttribute("action", "list");
					
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Communication with Google Drive failed", e);
          throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleCommunicationError"), e);
				}
      }

      context.setIncludeJSP("/jsp/forge/importgoogledocumentsdialog.jsp");
    }
	}

}