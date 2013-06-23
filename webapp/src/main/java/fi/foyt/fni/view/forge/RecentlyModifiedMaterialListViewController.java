package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class RecentlyModifiedMaterialListViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
  private ForgeWorkspaceManager workspaceManager;

  @Inject
	@DAO
  private MaterialDAO materialDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    List<Material> recentlyModifiedMaterials = materialDAO.listByModifierExcludingTypesSortByModified(loggedUser, Arrays.asList(new MaterialType[] {MaterialType.FOLDER}), 0, 5);
    List<WorkspaceMaterialBean> recentlyModifiedMaterialBeans = new ArrayList<WorkspaceMaterialBean>();

    for (Material recentlyModifiedMaterial : recentlyModifiedMaterials) {
      recentlyModifiedMaterialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, recentlyModifiedMaterial));
    }
    
    context.getRequest().setAttribute("recentlyModifiedMaterials", recentlyModifiedMaterialBeans);
    context.getRequest().setAttribute("loggedUser", loggedUser);

    context.setIncludeJSP("/jsp/forge/modifiedmateriallist.jsp");
  }
  
}