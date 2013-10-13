package fi.foyt.fni.view.forge.old;

import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

//@RequestScoped
//@Stateful
public class EditFolderDialogViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private FolderDAO folderDAO;

	@Override
  public boolean checkPermissions(ViewControllerContext context) {
  	Long folderId = context.getLongParameter("folderId");
  	Folder folder = folderDAO.findById(folderId);
  	User loggedUser = sessionController.getLoggedUser();
  	
    return materialPermissionController.hasModifyPermission(loggedUser, folder);
  }

  @Override
  public void execute(ViewControllerContext context) {
  	Long folderId = context.getLongParameter("folderId");
  	Folder folder = folderDAO.findById(folderId);

  	context.getRequest().setAttribute("folder", folder);
  	
    context.setIncludeJSP("/jsp/forge/editfolderdialog.jsp");
  }
}