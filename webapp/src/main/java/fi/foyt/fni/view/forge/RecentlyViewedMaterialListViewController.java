package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class RecentlyViewedMaterialListViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

  @Inject
  private ForgeWorkspaceManager workspaceManager;
  
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    List<Material> viewedMaterials = materialController.listViewedMaterialsByUser(loggedUser, 0, 5);
    List<WorkspaceMaterialBean> recentlyViewedMaterialBeans = new ArrayList<WorkspaceMaterialBean>();

    for (Material viewedMaterial : viewedMaterials) {
      recentlyViewedMaterialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, viewedMaterial));
    }
    
    context.getRequest().setAttribute("recentlyViewedMaterials", recentlyViewedMaterialBeans);
    context.getRequest().setAttribute("loggedUser", loggedUser);

    context.setIncludeJSP("/jsp/forge/viewedmateriallist.jsp");
  }
  
}