package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-images", 
   	pattern = "/forge/images/#{forgeImagesBackingBean.ownerId}/#{ /[a-zA-Z0-9_\\/\\.\\\\-\\:\\,]*/ forgeImagesBackingBean.urlPath }", 
		viewId = "/forge/images.jsf"
  ) 
})
public class ForgeImagesBackingBean {
	
	@Inject
	private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
	
	@URLAction
	@LoggedIn
	public void load() throws FileNotFoundException {
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			throw new FileNotFoundException();
		}
		
    String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
    Material material = materialController.findMaterialByCompletePath(completePath);

		if (!(material instanceof Image)) {
			throw new FileNotFoundException();
		}

    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
      throw new ForbiddenException();
    }
		
		materialPath = material.getPath();
		materialId = material.getId();
		imageTitle = material.getTitle();
		folders = ForgeViewUtils.getParentList(material);
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
	
	public String getImageTitle() {
		return imageTitle;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public String getMaterialPath() {
		return materialPath;
	}
	
	private Long ownerId;
	private String urlPath;
	private Long materialId;
	private String imageTitle;
	private List<Folder> folders;
	private String materialPath;
}
