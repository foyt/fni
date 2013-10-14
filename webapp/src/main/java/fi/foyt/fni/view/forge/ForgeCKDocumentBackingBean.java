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
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-ckdocument", 
		pattern = "/forge/ckdocument/#{forgeCKDocumentBackingBean.ownerId}/#{ /[a-z0-9_\\/]*/ forgeCKDocumentBackingBean.urlName }", 
		viewId = "/forge/ckdocument.jsf"
  ) 
})
public class ForgeCKDocumentBackingBean {
	
	@Inject
	private UserController userController;
	
	@Inject
	private MaterialController materialController;
	
	@URLAction
	public void load() throws FileNotFoundException {
		// TODO: Security
		
		if ((getOwnerId() == null)||(getUrlName() == null)) {
			throw new FileNotFoundException();
		}
		
		User owner = userController.findUserById(getOwnerId());
		if (owner == null) {
			throw new FileNotFoundException();
		}
		
		Material material = materialController.findByOwnerAndPath(owner, getUrlName());
		if (material == null) {
			throw new FileNotFoundException();
		}
		
		materialId = material.getId();
	}
	
	public Long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	public Long getMaterialId() {
		return materialId;
	}
	
	private Long ownerId;
	private String urlName;
	private Long materialId;
}
