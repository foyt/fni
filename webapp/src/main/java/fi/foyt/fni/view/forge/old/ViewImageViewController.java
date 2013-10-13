package fi.foyt.fni.view.forge.old;

import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

//@RequestScoped
//@Stateful
public class ViewImageViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private MaterialDAO materialDAO;

  @Inject
	private ImageDAO imageDAO;
  
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    Long materialId = context.getLongParameter("materialId");
    Material material = materialDAO.findById(materialId);
    return materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material);
  }

  @Override
  public void execute(ViewControllerContext context) {
    Long materialId = context.getLongParameter("materialId");
    Material material = materialDAO.findById(materialId);
    context.getRequest().setAttribute("material", material);
    context.setIncludeJSP("/jsp/forge/viewimage.jsp");
  }
}