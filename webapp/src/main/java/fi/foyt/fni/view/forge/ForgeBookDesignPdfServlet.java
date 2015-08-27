package fi.foyt.fni.view.forge;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.BookDesign;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;

@WebServlet ( urlPatterns = "/forge/bookDesignPdf/*", name = "forge-book-design-pdf")
@Transactional
public class ForgeBookDesignPdfServlet extends HttpServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private MaterialController materialController;
	
	@Inject
	private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		String bookDesignIdStr = StringUtils.removeStart(pathInfo, "/");
		if (!StringUtils.isNumeric(bookDesignIdStr)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
			return;
		}
		
		Long bookDesignId = NumberUtils.createLong(bookDesignIdStr);
		BookDesign bookDesign = materialController.findBookDesign(bookDesignId);
		if (bookDesign == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}
    
    User loggedUser = sessionController.getLoggedUser();
    if (!materialPermissionController.isPublic(loggedUser, bookDesign)) {
      if (loggedUser == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
      
      if (!materialPermissionController.hasAccessPermission(loggedUser, bookDesign)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }
		
		String baseUrl = request.getRequestURL().toString();
		baseUrl = baseUrl.substring(0, baseUrl.length() - (request.getRequestURI().length()));
		
		TypedData pdfData = materialController.printBookDesignAsPdf(sessionController.getLoggedUser(), bookDesign);
		if (pdfData == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}

		if (StringUtils.isNotBlank(bookDesign.getUrlName())) {
			response.setHeader("content-disposition", "attachment; filename=" + bookDesign.getUrlName() + ".pdf");
		}

		response.setContentType(pdfData.getContentType());

		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(pdfData.getData());
		} finally {
			outputStream.flush();
		}
	}
}
