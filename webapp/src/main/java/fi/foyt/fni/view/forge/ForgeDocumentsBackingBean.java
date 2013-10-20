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
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.users.UserController;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-documents", 
		pattern = "/forge/documents/#{forgeDocumentsBackingBean.ownerId}/#{ /[a-zA-Z0-9_\\/\\.\\-]*/ forgeDocumentsBackingBean.urlPath }", 
		viewId = "/forge/documents.jsf"
  ) 
})
public class ForgeDocumentsBackingBean {
	
	@Inject
	private UserController userController;
	
	@Inject
	private MaterialController materialController;
	
	@URLAction
	@LoggedIn
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
		
		if (!(material instanceof Document)) {
			throw new FileNotFoundException();
		}
		
		materialId = material.getId();
		documentTitle = material.getTitle();
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
	
	public String getDocumentTitle() {
		return documentTitle;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	private Long ownerId;
	private String urlPath;
	private Long materialId;
	private String documentTitle;
	private List<Folder> folders;
}
