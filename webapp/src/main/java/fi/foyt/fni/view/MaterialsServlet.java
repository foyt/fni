package fi.foyt.fni.view;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.servlet.RequestUtils;

@WebServlet ( urlPatterns = "/materials/*", name = "materials")
@Transactional
public class MaterialsServlet extends HttpServlet {
  
	private static final long serialVersionUID = -5739692573670665390L;
  private static final long DEFAULT_EXPIRE_TIME = 1000 * 60 * 60;

	@Inject
	private Logger logger;
	
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
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
    }

    User loggedUser = sessionController.getLoggedUser();
    if (!(materialPermissionController.isPublic(loggedUser, material) || materialPermissionController.hasAccessPermission(loggedUser, material))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
    }

    String eTag = createETag(material);
    long lastModified = material.getModified().getTime();
    if (!RequestUtils.isModifiedSince(request, lastModified, eTag)) {
      response.setHeader("ETag", eTag);
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    }
    
    FileData data;
    try {
      data = materialController.getMaterialData(request.getContextPath(), sessionController.getLoggedUser(), material);
    } catch (GeneralSecurityException e) {
      logger.log(Level.SEVERE, "Could not retrieve material data because of a general security error", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
		
    if (data == null) {
  		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  		return;
    }
    
    if ("true".equals(request.getParameter("download"))) {
		  response.setHeader("content-disposition", "attachment; filename=" + data.getFileName());
    }
		
    response.setHeader("ETag", eTag);
    response.setDateHeader("Last-Modified", lastModified);
    response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

		response.setContentType(data.getContentType());
		if (data.getData() != null) {
  		ServletOutputStream outputStream = response.getOutputStream();
  		try {
  			outputStream.write(data.getData());
  		} finally {
  			outputStream.flush();
  		}
		}
	}

  private String createETag(Material material) {
    return String.valueOf(material.getModified().getTime());
  }
	
}
