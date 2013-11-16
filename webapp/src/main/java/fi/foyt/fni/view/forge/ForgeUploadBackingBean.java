package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import fi.foyt.fni.materials.FolderController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "forge-upload", 
		pattern = "/forge/upload", 
		viewId = "/forge/upload.jsf"
  )
})
public class ForgeUploadBackingBean {

  @Inject
  private FolderController folderController;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;

	@URLAction
	@LoggedIn
	public void load() throws FileNotFoundException {
    if (parentFolderId != null) {
      Folder parentFolder = parentFolderId != null ? folderController.findFolderById(parentFolderId) : null;
      if (parentFolder != null) {
        if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
          throw new ForbiddenException();
        }
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

  @URLQueryParameter ("parentFolderId")
  private Long parentFolderId;
}
