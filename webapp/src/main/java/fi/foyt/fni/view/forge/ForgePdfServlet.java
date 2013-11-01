package fi.foyt.fni.view.forge;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/pdf/*")
public class ForgePdfServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private DocumentController documentController;
	
	@Inject
	private UserController userController;
	
	@Inject
	private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		String documentIdStr = StringUtils.removeStart(pathInfo, "/");
		if (!StringUtils.isNumeric(documentIdStr)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
			return;
		}
		
		Long documentId = NumberUtils.createLong(documentIdStr);
		Document document = documentController.findDocumentById(documentId);
		if (document == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}
    
    User loggedUser = sessionController.getLoggedUser();
    if (!materialPermissionController.isPublic(loggedUser, document)) {
      if (loggedUser == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
      
      if (!materialPermissionController.hasAccessPermission(loggedUser, document)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }
		
		String contextPath = request.getContextPath();
		String baseUrl = request.getRequestURL().toString();
		baseUrl = baseUrl.substring(0, baseUrl.length() - (request.getRequestURI().length()));
		
		TypedData pdfData;
		try {
			pdfData = documentController.printDocumentAsPdf(contextPath, baseUrl, sessionController.getLoggedUser(), document);
		} catch (DocumentException | ParserConfigurationException | SAXException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}
		
		if (pdfData == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}

		if (StringUtils.isNotBlank(document.getUrlName())) {
			response.setHeader("content-disposition", "attachment; filename=" + document.getUrlName() + ".pdf");
		}

		response.setContentType(pdfData.getContentType());

		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(pdfData.getData());
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
}
