package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.materials.FolderController;
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
  private FolderController folderController;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;

	@RequestAction
	public void load() throws FileNotFoundException {
    folders = new ArrayList<>();
    if (parentFolderId != null) {
      Folder parentFolder = parentFolderId != null ? folderController.findFolderById(parentFolderId) : null;
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
        throw new FileNotFoundException();
      }
    }
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

  private List<Folder> folders;
}
