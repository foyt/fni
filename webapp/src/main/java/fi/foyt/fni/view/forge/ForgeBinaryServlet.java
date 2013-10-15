package fi.foyt.fni.view.forge;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Binary;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/binary/*")
public class ForgeBinaryServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private MaterialController materialController;
	
	@Inject
	private UserController userController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: Security
		// TODO: Caching
		
		String pathInfo = StringUtils.removeStart(request.getPathInfo(), "/");
		String[] pathElements = pathInfo.split("/", 2);
		if (pathElements.length != 2) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
			return;
		}
		
		if (!StringUtils.isNumeric(pathElements[0])) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
			return;
		}
		
		Long ownerId = NumberUtils.createLong(pathElements[0]);
		User owner = userController.findUserById(ownerId);
		if (owner == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}
		
		String materialPath = pathElements[1];
				
		Material material = materialController.findByOwnerAndPath(owner, materialPath);
		if (!(material instanceof Binary)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}
		
		Binary binary = (Binary) material;

		if (StringUtils.isNotBlank(binary.getUrlName())) {
			response.setHeader("content-disposition", "attachment; filename=" + binary.getUrlName());
		}
		
		response.setContentType(binary.getContentType());

		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(binary.getData());
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
}
