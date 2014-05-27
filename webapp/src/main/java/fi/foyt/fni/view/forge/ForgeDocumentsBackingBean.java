package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/documents/{ownerId}/{urlPath}", to = "/forge/documents.jsf")
@LoggedIn
public class ForgeDocumentsBackingBean {
  
  @Parameter
  @Matches ("[0-9]{1,}")
  private Long ownerId;
  
  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String urlPath;
  
	@Inject
	private SessionController sessionController;
	
	@Inject
	private MaterialController materialController;

  @Inject
	private MaterialPermissionController materialPermissionController;
	
	@RequestAction
	@Deferred
	public String load() throws FileNotFoundException {
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			return "/error/not-found.jsf";
		}
		
		String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
		Material material = materialController.findMaterialByCompletePath(completePath);
		User loggedUser = sessionController.getLoggedUser();
		
		if (!(material instanceof Document)) {
      return "/error/not-found.jsf";
		}
		
		if (!materialPermissionController.hasAccessPermission(loggedUser, material)) {
      return "/error/access-denied.jsf";
		}
		
		readOnly = !materialPermissionController.hasModifyPermission(loggedUser, material);
		materialId = material.getId();
		documentTitle = material.getTitle();
		folders = ForgeViewUtils.getParentList(material);
		documentReadOnlyLink = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + completePath;
		
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
	
	public String getDocumentTitle() {
		return documentTitle;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public Boolean getReadOnly() {
    return readOnly;
  }
	
	public String getDocumentReadOnlyLink() {
    return documentReadOnlyLink;
  }
	
	private Long materialId;
	private String documentTitle;
	private List<Folder> folders;
  private Boolean readOnly;
  private String documentReadOnlyLink;
}
