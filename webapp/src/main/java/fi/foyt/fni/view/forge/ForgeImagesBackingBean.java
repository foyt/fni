package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join ( path = "/forge/images/{ownerId}/{urlPath}", to = "/forge/images.jsf")
@LoggedIn
public class ForgeImagesBackingBean {

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

  @Inject
  private NavigationController navigationController;
	
	@RequestAction
	public String load() throws FileNotFoundException {
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			return navigationController.notFound();
		}
		
    String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
    Material material = materialController.findMaterialByCompletePath(completePath);

		if (!(material instanceof Image)) {
      return navigationController.notFound();
		}

    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
      return navigationController.accessDenied();
    }
		
		materialPath = material.getPath();
		materialId = material.getId();
		imageTitle = material.getTitle();
		folders = ForgeViewUtils.getParentList(material);
		
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
	
	public String getImageTitle() {
		return imageTitle;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public String getMaterialPath() {
		return materialPath;
	}
	
	private Long materialId;
	private String imageTitle;
	private List<Folder> folders;
	private String materialPath;
}
