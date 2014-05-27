package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.materials.GoogleDriveMaterialController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/google-drive/{ownerId}/{urlPath}", to = "/forge/googledrive.jsf")
@LoggedIn
public class ForgeGoogleDriveBackingBean {

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long ownerId;

  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String urlPath;
  
	@Inject
	private MaterialController materialController;

	@Inject
	private GoogleDriveMaterialController googleDriveMaterialController;
	
	@Inject
	private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;
	
	@RequestAction
	public String load() throws IOException, GeneralSecurityException {
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			return "/error/not-found.jsf";
		}
		
    String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
    Material material = materialController.findMaterialByCompletePath(completePath);

		if (!(material instanceof GoogleDocument)) {
		  return "/error/not-found.jsf";
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
	  
	  return null;
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
	
	private Long materialId;
	private String materialTitle;
	private String materialPath;
	private String googleDriveEditLink;
	private List<Folder> folders;
}
