package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.ComparatorUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.GoogleDriveType;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/import-google-drive", to = "/forge/import-google-drive.jsf")
@LoggedIn
public class ForgeImportGoogleDriveBackingBean {

  private final static String REQUIRED_SCOPE = "https://www.googleapis.com/auth/drive";
  
  @Parameter
  private String folderId;

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long parentFolderId;
  
  @Inject
  private Logger logger;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @Inject
  private DriveManager driveManager;

  @Inject
  private NavigationController navigationController;
  
  @SuppressWarnings("unchecked")
  @RequestAction
  @Deferred
	public String load() throws IOException {
    UserToken userToken = sessionController.getLoggedUserToken();
    User loggedUser = userToken.getUserIdentifier().getUser();
    String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    
    if (parentFolderId != null) {
      Folder parentFolder = parentFolderId != null ? materialController.findFolderById(parentFolderId) : null;
      if (parentFolder != null) {
        if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
          return navigationController.accessDenied();
        }
      } else {
        return navigationController.notFound();
      }
    }

    if (AuthUtils.isExpired(userToken) || !AuthUtils.isGrantedScope(userToken, REQUIRED_SCOPE)) {
      // We need authorization from Google
      
      String redirectUrl = contextPath + "/forge/import-google-drive";
      if (parentFolderId != null) {
        redirectUrl += "?parentFolderId=" + parentFolderId;
      }
      
      return "/users/login.jsf?faces-redirect=true&loginMethod=GOOGLE&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8") + "&extraScopes=" +  REQUIRED_SCOPE;
    } else {
      Drive drive = driveManager.getDrive(driveManager.getAccessTokenCredential(userToken.getToken()));
      FileList fileList = null;
      root = folderId == null;
      files = new ArrayList<File>();
      
      if (folderId == null) {
        fileList = driveManager.listFiles(drive, "trashed != true and 'root' in parents");
      } else {
        fileList = driveManager.listFiles(drive, "trashed != true and '" + folderId + "' in parents");
      }
      
      for (File file : fileList.getItems()) {
        if (!file.getMimeType().equals(GoogleDriveType.FORM.getMimeType())) {
          if (materialController.findGoogleDocumentByCreatorAndDocumentId(loggedUser, file.getId()) == null) {
            files.add(file);
          }
        }
      }

      Collections.sort(files, ComparatorUtils.chainedComparator(
        Arrays.asList(
          new MimeTypeComparator("application/vnd.google-apps.folder"),
          new TitleComparator()
        )
      ));
    }
    
    return null;
	}

	public boolean isRoot() {
    return root;
  }
	
	public String getFolderId() {
    return folderId;
  }
	
	public void setFolderId(String folderId) {
    this.folderId = folderId;
  }
	
	public List<File> getFiles() {
    return files;
  }
	
	public List<String> getImportEntryIds() {
    return importEntryIds;
  }
	
	public void setImportEntryIds(List<String> importEntryIds) {
    this.importEntryIds = importEntryIds;
  }

	public void importFiles() throws IOException {
    Folder parentFolder = parentFolderId != null ? materialController.findFolderById(parentFolderId) : null;
    if (parentFolder != null) {
      if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
        throw new ForbiddenException();
      }
    }
    
	  String accountUser = System.getProperty("fni-google-drive.accountUser");
    UserToken userToken = sessionController.getLoggedUserToken();
    Drive drive = driveManager.getDrive(driveManager.getAccessTokenCredential(userToken.getToken()));
    User loggedUser = userToken.getUserIdentifier().getUser();
    
    for (String entryId : importEntryIds) {
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
          FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("forge.googleDriveImport.importFailure"));
        }
      }
    }
    
    if (parentFolder != null) {
      FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
        .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
        .append("/forge/folders/")
        .append(parentFolder.getPath())
        .toString());
    } else {
      FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
        .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
        .append("/forge/")
        .toString());
    }
	}
	
	public Long getParentFolderId() {
    return parentFolderId;
  }
	
	public void setParentFolderId(Long parentFolderId) {
    this.parentFolderId = parentFolderId;
  }
	
	public String getFileIcon(File file) {
	  switch (file.getMimeType()) {
	    case "application/vnd.google-apps.folder":
	      return "folder";
	    case "application/vnd.google-apps.document":
	      return "document";
	    case "application/vnd.google-apps.presentation":
	      return "presentation";
	    case "application/vnd.google-apps.spreadsheet":
	      return "spreadsheet";
	    case "application/vnd.google-apps.drawing":
	      return "drawing";	      
	  }
	  
	  return "file";
	}

	private boolean root;
  private List<File> files;
  private List<String> importEntryIds;
  
  private class MimeTypeComparator implements Comparator<File> {
    
    public MimeTypeComparator(String mimeType) {
      this.mimeType = mimeType;
    }
    
    @Override
    public int compare(File o1, File o2) {
      if (o1.getMimeType().equals(o2.getMimeType()))
        return 0;
      
      if (o1.getMimeType().equals(mimeType))
        return -1;
      
      if (o2.getMimeType().equals(mimeType))
        return 1;

      return 0;
    }
    
    private String mimeType;
  }
  
  private class TitleComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
      return file1.getTitle().compareTo(file2.getTitle());
    }

  }
}
