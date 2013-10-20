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
import fi.foyt.fni.materials.VectorImageController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-vectorimages", 
   	pattern = "/forge/vectorimages/#{forgeVectorImagesBackingBean.ownerId}/#{ /[a-zA-Z0-9_\\/\\.\\-]*/ forgeVectorImagesBackingBean.urlPath }", 
		viewId = "/forge/vectorimages.jsf"
  ) 
})
public class ForgeVectorImagesBackingBean {
	
	@Inject
	private UserController userController;

	@Inject
	private MaterialController materialController;

	@Inject
	private VectorImageController vectorImageController;
	
	@URLAction
	public void load() throws FileNotFoundException {
		// TODO: Security
		
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			throw new FileNotFoundException();
		}
		
		User owner = userController.findUserById(getOwnerId());
		if (owner == null) {
			throw new FileNotFoundException();
		}
		
		Material material = materialController.findByOwnerAndPath(owner, getUrlPath());
		if (material == null) {
			throw new FileNotFoundException();
		}
		
		if (!(material instanceof VectorImage)) {
			throw new FileNotFoundException();
		}
		
		VectorImage vectorImage = (VectorImage) material;
		
		materialId = material.getId();
		vectorImageTitle = material.getTitle();
		vectorImageContent = vectorImage.getData();
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
	
	public String getVectorImageTitle() {
		return vectorImageTitle;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public String getVectorImageContent() {
		return vectorImageContent;
	}
	
	private Long ownerId;
	private String urlPath;
	private Long materialId;
	private String vectorImageTitle;
	private String vectorImageContent;
	private List<Folder> folders;
}
