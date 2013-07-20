package fi.foyt.fni.view.forge;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class EditVectorImageViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private VectorImageDAO vectorImageDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    if (loggedUser == null) {
    	return false;
    }
    
    if ("NEW".equals(context.getStringParameter("vectorImageId"))) {
    	return true;
    }
    
    Long vectorImageId = context.getLongParameter("vectorImageId");
    VectorImage vectorImage = vectorImageDAO.findById(vectorImageId);
    
  	return materialPermissionController.hasModifyPermission(loggedUser, vectorImage);
  }

  @Override
  public void execute(ViewControllerContext context) {
    if ("NEW".equals(context.getStringParameter("vectorImageId"))) {
      context.getRequest().setAttribute("vectorImageData", "");
      context.getRequest().setAttribute("vectorImageId", "NEW");
      context.getRequest().setAttribute("vectorImageTitle", Locales.getText(context.getRequest().getLocale(), "forge.editVectorImage.untitledDocument"));
      context.getRequest().setAttribute("parentFolder", null);
    } else {
      Long vectorImageId = context.getLongParameter("vectorImageId");
      if (vectorImageId == null) {
      	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "generic.error.missingParameter", "vectorImageId"));
      }
      
      VectorImage vectorImage = vectorImageDAO.findById(vectorImageId);
      
      context.getRequest().setAttribute("vectorImageData", vectorImage.getData());
      context.getRequest().setAttribute("vectorImageId", vectorImage.getId());
      context.getRequest().setAttribute("vectorImageTitle", vectorImage.getTitle());
      context.getRequest().setAttribute("parentFolder", vectorImage.getParentFolder());
    }
    
    context.setIncludeJSP("/jsp/forge/editvectorimage.jsp");
  }
}