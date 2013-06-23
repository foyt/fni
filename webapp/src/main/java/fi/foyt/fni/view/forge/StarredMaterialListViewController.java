package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.StarredMaterialDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class StarredMaterialListViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
  private ForgeWorkspaceManager workspaceManager;

  @Inject
  private MaterialController materialController;
  
  @Inject
  @DAO
	private StarredMaterialDAO starredMaterialDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    boolean showAll = "1".equals(context.getStringParameter("showAll"));
    
    List<Material> starredMaterials;
    if (showAll) {
      starredMaterials = materialController.listStarredMaterialsByUser(loggedUser);
    } else {
      starredMaterials = materialController.listStarredMaterialsByUser(loggedUser, 0, 5);
    }
    
    List<WorkspaceMaterialBean> starredMaterialBeans = new ArrayList<WorkspaceMaterialBean>();

    for (Material starredMaterial : starredMaterials) {
      starredMaterialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, starredMaterial));
    }
    
    Long starredMaterialCount;
    if (showAll) {
      starredMaterialCount = new Long(starredMaterials.size()); 
    } else {
      starredMaterialCount = starredMaterials.size() >= 5 ? starredMaterialDAO.countByUser(loggedUser) : starredMaterials.size(); 
    }
    
    context.getRequest().setAttribute("showAll", showAll);
    context.getRequest().setAttribute("starredMaterialCount", starredMaterialCount);
    context.getRequest().setAttribute("starredMaterials", starredMaterialBeans);
    context.getRequest().setAttribute("loggedUser", loggedUser);
    
    context.setIncludeJSP("/jsp/forge/starredmateriallist.jsp");
  }
  
}