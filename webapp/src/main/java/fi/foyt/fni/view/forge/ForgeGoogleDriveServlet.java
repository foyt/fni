package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.GoogleDriveMaterialController;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/gdrive/*")
public class ForgeGoogleDriveServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private GoogleDriveMaterialController googleDriveMaterialController;
	
	@Inject
	private UserController userController;
	
	@Inject
	private SessionController sessionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: Security
		// TODO: Caching
		
		String pathInfo = request.getPathInfo();
		String googleDocumentIdStr = StringUtils.removeStart(pathInfo, "/");
		if (!StringUtils.isNumeric(googleDocumentIdStr)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
			return;
		}
		
		Long googleDocumentId = NumberUtils.createLong(googleDocumentIdStr);
		GoogleDocument googleDocument = googleDriveMaterialController.findGoogleDocumentById(googleDocumentId);
		if (googleDocument == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}
		
		TypedData data;
		try {
			data = googleDriveMaterialController.getGoogleDocumentData(googleDocument);
		} catch (MalformedURLException | GeneralSecurityException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}
		
		if (data == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}

		response.setContentType(data.getContentType());
		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(data.getData());
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
}
