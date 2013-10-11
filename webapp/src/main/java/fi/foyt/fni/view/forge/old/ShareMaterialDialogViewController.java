package fi.foyt.fni.view.forge.old;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.UserMaterialRoleDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

//@RequestScoped
//@Stateful
public class ShareMaterialDialogViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
  private MaterialDAO materialDAO;

	@Inject
  private UserMaterialRoleDAO userMaterialRoleDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
  	Long materialId = context.getLongParameter("materialId");
  	Material material = materialDAO.findById(materialId);
  	User loggedUser = sessionController.getLoggedUser();
  	
    return materialPermissionController.hasModifyPermission(loggedUser, material);
  }

  @Override
  public void execute(ViewControllerContext context) {
  	Long materialId = context.getLongParameter("materialId");
  	Material material = materialDAO.findById(materialId);
  	List<UserMaterialRole> materialUserRoles = userMaterialRoleDAO.listByMaterial(material);
  	
  	context.getRequest().setAttribute("publicUrl", context.getBasePath() + "/" + material.getPath());
  	context.getRequest().setAttribute("material", material);
  	context.getRequest().setAttribute("materialUserRoles", materialUserRoles);
  	
    context.setIncludeJSP("/jsp/forge/sharematerialdialog.jsp");
  }
}