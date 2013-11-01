package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.materials.GoogleDriveMaterialController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-google-drive", 
   	pattern = "/forge/google-drive/#{forgeGoogleDriveBackingBean.ownerId}/#{ /[a-zA-Z0-9_\\/\\.\\\\-\\:]*/ forgeGoogleDriveBackingBean.urlPath }", 
		viewId = "/forge/googledrive.jsf"
  ) 
})
public class ForgeGoogleDriveBackingBean {
	
	@Inject
	private UserController userController;

	@Inject
	private MaterialController materialController;

	@Inject
	private GoogleDriveMaterialController googleDriveMaterialController;
	
	@Inject
	private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;
	
	@URLAction
	@LoggedIn
	public void load() throws IOException, GeneralSecurityException {
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			throw new FileNotFoundException();
		}
		
    String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
    Material material = materialController.findMaterialByCompletePath(completePath);

		if (!(material instanceof GoogleDocument)) {
			throw new FileNotFoundException();
		}

		if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
      throw new ForbiddenException();
    }
    
		GoogleDocument googleDocument = (GoogleDocument) material;
    
		materialId = googleDocument.getId();
		materialTitle = googleDocument.getTitle();
		materialPath = "/forge/gdrive/" + materialId; 
		folders = ForgeViewUtils.getParentList(googleDocument);
		
	  googleDriveEditLink = googleDriveMaterialController.getGoogleDocumentEditLink(googleDocument);
	}
	
	public Long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getUrlPath() {
		return urlPath;
	}
	
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	
	public Long getMaterialId() {
		return materialId;
	}
	
	public String getMaterialTitle() {
		return materialTitle;
	}
	
	public void setMaterialTitle(String materialTitle) {
		this.materialTitle = materialTitle;
	}
	
	public String getMaterialPath() {
		return materialPath;
	}
	
	public void setMaterialPath(String materialPath) {
		this.materialPath = materialPath;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public String getGoogleDriveEditLink() {
		return googleDriveEditLink;
	}
	
	private Long ownerId;
	private String urlPath;
	private Long materialId;
	private String materialTitle;
	private String materialPath;
	private String googleDriveEditLink;
	private List<Folder> folders;
}
