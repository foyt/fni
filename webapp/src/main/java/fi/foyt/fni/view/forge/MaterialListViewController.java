package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class MaterialListViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;
  
  @Inject
  private ForgeWorkspaceManager workspaceManager;

	@Inject
	private FolderDAO folderDAO;
	
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    Long parentFolderId = context.getLongParameter("parentFolderId");
    
    Folder folder = parentFolderId == null ? null : folderDAO.findById(parentFolderId);
    List<Material> materials = materialController.listMaterialsByFolder(loggedUser, folder);
    Collections.sort(materials, new TitleComparator());
    
    List<WorkspaceMaterialBean> materialBeans = new ArrayList<WorkspaceMaterialBean>();
    
    for (Material material : materials) {
      materialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, material));
    }
    
    workspaceManager.sortMaterials(materialBeans);
    
    List<WorkspaceMaterialBean> parentFolders = new ArrayList<WorkspaceMaterialBean>();
    
    Folder currentFolder = folder;
    while (currentFolder != null) {
      WorkspaceMaterialBean folderBean = workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, currentFolder);
      parentFolders.add(folderBean);
      currentFolder = currentFolder.getParentFolder();
    }
    parentFolders.add(workspaceManager.createRootBean(context.getRequest().getLocale()));

    Collections.reverse(parentFolders);
    
    context.getRequest().setAttribute("parentFolders", parentFolders);
    context.getRequest().setAttribute("materials", materialBeans);
    context.getRequest().setAttribute("loggedUser", loggedUser);
    
    context.setIncludeJSP("/jsp/forge/materiallist.jsp");
  }
  
}