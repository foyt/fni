package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-images", 
   	pattern = "/forge/images/#{forgeImagesBackingBean.ownerId}/#{ /[a-zA-Z0-9_\\/\\.\\-]*/ forgeImagesBackingBean.urlPath }", 
		viewId = "/forge/images.jsf"
  ) 
})
public class ForgeImagesBackingBean {
	
	@Inject
	private UserController userController;
	
	@Inject
	private MaterialController materialController;
	
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
		
		if (!(material instanceof Image)) {
			throw new FileNotFoundException();
		}
		
		materialId = material.getId();
		imageTitle = material.getTitle();
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
	
	private Long ownerId;
	private String urlPath;
	private Long materialId;
	private String imageTitle;
}
