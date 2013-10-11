package fi.foyt.fni.view.forge.old;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

//@RequestScoped
//@Stateful
public class ViewGoogleDocumentViewController extends AbstractViewController {

	@Inject
	private GoogleDocumentDAO googleDocumentDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    // TODO: Implement
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    Long materialId = context.getLongParameter("materialId");
    
    GoogleDocument googleDocument = googleDocumentDAO.findById(materialId);
    context.getRequest().setAttribute("googleDocument", googleDocument);
    
    context.setIncludeJSP("/jsp/forge/viewgoogledocument.jsp");
  }
}