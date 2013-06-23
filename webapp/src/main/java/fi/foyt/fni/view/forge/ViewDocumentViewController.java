package fi.foyt.fni.view.forge;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class ViewDocumentViewController extends AbstractViewController {

	@Inject
	private SessionController sessionController;
	
	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private MaterialController materialController;
	
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
  	Long materialId = context.getLongParameter("materialId");
  	
  	Material material = materialController.findMaterialById(materialId);
  	User loggedUser = sessionController.getLoggedUser();
  	
  	if (materialPermissionController.isPublic(loggedUser, material)) {
  		return true;
  	}
  	
  	if (materialPermissionController.hasAccessPermission(loggedUser, material)) {
  		return true;
  	}
  	
    return false;
  }

  @Override
  public void execute(ViewControllerContext context) {
    Long materialId = context.getLongParameter("materialId");
    Material material = materialController.findMaterialById(materialId);
    context.getRequest().setAttribute("path", material.getPath());
    context.setIncludeJSP("/jsp/forge/viewdocument.jsp");
  }
}