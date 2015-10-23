package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/upload", to = "/forge/upload.jsf")
@LoggedIn
public class ForgeUploadBackingBean {
  
  @Parameter
  private Long parentFolderId;
  
  @Inject
  private Logger logger;

  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private NavigationController navigationController;

	@RequestAction
	public String load() {
    folders = new ArrayList<>();
    if (parentFolderId != null) {
      Folder parentFolder = parentFolderId != null ? materialController.findFolderById(parentFolderId) : null;
      if (parentFolder != null) {
        if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
          throw new ForbiddenException();
        }
        
        Folder folder = parentFolder;
        while (folder != null) {
          folders.add(0, folder);
          folder = folder.getParentFolder();
        };
      } else {
        return navigationController.notFound();
      }
    }
    
    return null;
	}
	
	public Long getParentFolderId() {
    return parentFolderId;
  }
	
	public void setParentFolderId(Long parentFolderId) {
    this.parentFolderId = parentFolderId;
  }

  public List<Folder> getFolders() {
    return folders;
  }
  
  public String uploadDone() {
    Folder parentFolder = parentFolderId != null ? materialController.findFolderById(parentFolderId) : null;
    try {
      if (parentFolder != null) {
        FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
          .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
          .append("/forge/folders/")
          .append(parentFolder.getPath())
          .toString());
      } else {
        return "/forge/index.jsf?faces-redirect=true";
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "After upload redirect failed", e);
      return navigationController.internalError();
    }
    
    return null;
  }

  private List<Folder> folders;
}
