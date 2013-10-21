package fi.foyt.fni.view.forge;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
public class ForgeMaterialActionBackingBean {

	@Inject
	private MaterialController materialController;

	@Inject
	private SessionController sessionController;


	public void deleteMaterial() throws IOException {
		// TODO: Security 
		
		Material material = materialController.findMaterialById(getMaterialId());
		if (material != null) {
			Folder parentFolder = material.getParentFolder();
			materialController.deleteMaterial(material, sessionController.getLoggedUser());
			
			if (parentFolder != null) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
    		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
    		  .append("/forge/folders/")
    		  .append(parentFolder.getPath())
    		  .toString());
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
    		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
    		  .append("/forge/")
    		  .toString());
			}
		}
	}
	
	public Long getMaterialId() {
		return materialId;
	}
	
	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}
	
	private Long materialId;
}
