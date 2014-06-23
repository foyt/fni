package fi.foyt.fni.view.forge;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@WebServlet(urlPatterns = "/forge/binary/*", name = "forge-binary")
@Transactional
public class ForgeBinaryServlet extends HttpServlet {

	private static final long serialVersionUID = -1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		if (ownerId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}

		String materialPath = pathElements[1];
		response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
		response.setHeader("Location", request.getContextPath() + "/materials/" + ownerId + "/" + materialPath);
	}

}
