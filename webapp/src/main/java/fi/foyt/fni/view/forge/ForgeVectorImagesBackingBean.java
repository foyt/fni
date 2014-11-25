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

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
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
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/vectorimages/{ownerId}/{urlPath}", to = "/forge/vectorimages.jsf")
@LoggedIn
public class ForgeVectorImagesBackingBean {
  
  @Parameter
  @Matches ("[0-9]{1,}")
  private Long ownerId;

  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String urlPath;
	
	@Inject
	private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
	
	@RequestAction
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
		VectorImage vectorImage = materialController.findVectorImageById(materialId);
		if (getMaterialModified().equals(vectorImage.getModified().getTime())) {
			materialController.updateVectorImageData(vectorImage, getVectorImageContent(), sessionController.getLoggedUser());
			materialController.updateVectorImageTitle(vectorImage, getVectorImageTitle(), sessionController.getLoggedUser());
			
			FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  		  .append("/forge/vectorimages/")
  		  .append(vectorImage.getPath())
  		  .toString());
		} else {
			FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("forge.vectorImages.conflictError"));
		}
	}
	
	private Long materialId;
	private Long materialModified;
	private String vectorImageTitle;
	private String vectorImageContent;
	private Boolean readOnly;
	private List<Folder> folders;
}
