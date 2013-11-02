package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.VectorImageController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;

@SuppressWarnings("el-syntax")
@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
	@URLMapping(
	  id = "forge-vectorimages", 
   	pattern = "/forge/vectorimages/#{forgeVectorImagesBackingBean.ownerId}/#{ /[a-zA-Z0-9_\\/\\.\\\\-\\:\\,]*/ forgeVectorImagesBackingBean.urlPath }", 
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

		if (!(material instanceof VectorImage)) {
			throw new FileNotFoundException();
		}
		
		User loggedUser = sessionController.getLoggedUser();

    if (!materialPermissionController.hasAccessPermission(loggedUser, material)) {
      throw new ForbiddenException();
    }
    
    readOnly = !materialPermissionController.hasModifyPermission(loggedUser, material);
    
		VectorImage vectorImage = (VectorImage) material;
		
		materialId = vectorImage.getId();
		materialModified = vectorImage.getModified().getTime();
		vectorImageTitle = vectorImage.getTitle();
		vectorImageContent = vectorImage.getData();
		folders = ForgeViewUtils.getParentList(vectorImage);
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
	
	public void setVectorImageTitle(String vectorImageTitle) {
		this.vectorImageTitle = vectorImageTitle;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public String getVectorImageContent() {
		return vectorImageContent;
	}
	
	public void setVectorImageContent(String vectorImageContent) {
		this.vectorImageContent = vectorImageContent;
	}
	
	public Long getMaterialModified() {
		return materialModified;
	}
	
	public void setMaterialModified(Long materialModified) {
		this.materialModified = materialModified;
	}
	
	public Boolean getReadOnly() {
    return readOnly;
  }
	
	@LoggedIn
	@Secure (Permission.MATERIAL_MODIFY)
	@SecurityContext (context = "#{forgeVectorImagesBackingBean.materialId}")
	public void save() throws IOException {
		VectorImage vectorImage = vectorImageController.findVectorImageById(materialId);
		if (getMaterialModified().equals(vectorImage.getModified().getTime())) {
			vectorImageController.updateVectorImageData(vectorImage, getVectorImageContent(), sessionController.getLoggedUser());
			vectorImageController.updateVectorImageTitle(vectorImage, getVectorImageTitle(), sessionController.getLoggedUser());
			
			FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  		  .append("/forge/vectorimages/")
  		  .append(vectorImage.getPath())
  		  .toString());
		} else {
			FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("forge.vectorImages.conflictError"));
		}
	}
	
	private Long ownerId;
	private String urlPath;
	private Long materialId;
	private Long materialModified;
	private String vectorImageTitle;
	private String vectorImageContent;
	private Boolean readOnly;
	private List<Folder> folders;
}
