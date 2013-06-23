package fi.foyt.fni.view;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@WebServlet ( urlPatterns = "/materials/*")
public class MaterialsServlet extends AbstractViewServlet {

	private static final long serialVersionUID = -7538319095786895858L;
	
  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
	private MaterialController materialController;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Material material = materialController.findMaterialByCompletePath(RequestUtils.stripCtxPath(request.getContextPath(), request.getRequestURI()));
		if (material == null) {
    	handleNotFound(request, response);
      return;
    }

    User loggedUser = sessionController.getLoggedUser();
    if (!(materialPermissionController.isPublic(loggedUser, material) || materialPermissionController.hasAccessPermission(loggedUser, material))) {
    	handleForbidden(request, response, sessionController.isLoggedIn());
    }

    try {
			switch (material.getType()) {
				case IMAGE:
					handleInclude(request, response, "/v1/materials/images/" + material.getId());
				break;
				case DOCUMENT:
					handleInclude(request, response, "/v1/materials/documents/" + material.getId());
				break;
				case VECTOR_IMAGE:
					handleInclude(request, response, "/v1/materials/vectorImages/" + material.getId());
				break;
				case PDF:
					handleInclude(request, response, "/v1/materials/pdfs/" + material.getId());
				break;
				case FILE:
					handleInclude(request, response, "/v1/materials/files/" + material.getId());
				break;
				case GOOGLE_DOCUMENT:
					handleInclude(request, response, "/v1/materials/googleDocuments/" + material.getId());
				break;
				case DROPBOX_FILE:
					handleInclude(request, response, "/v1/materials/dropbox/" + material.getId());
				break;
				case UBUNTU_ONE_FILE:
					handleInclude(request, response, "/v1/materials/ubuntuOne/" + material.getId());
				break;
				default:
					throw new ViewControllerException(Locales.getText(request.getLocale(), "generic.error.internalError"));
			}
    } catch (IOException e) {
    	throw new ViewControllerException(Locales.getText(request.getLocale(), "generic.error.apiCommunicationError"));
    } catch (ServletException e) {
    	throw new ViewControllerException(Locales.getText(request.getLocale(), "generic.error.configurationError"));
    }
	}

	private void handleInclude(HttpServletRequest request, HttpServletResponse response, String includeUrl) throws ServletException, IOException {
		request.getRequestDispatcher(includeUrl).forward(request, response);
	}
	
}
