package fi.foyt.fni.view.forge;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.BookDesign;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.data.FileData;

@WebServlet ( urlPatterns = "/forge/bookDesignData/*", name = "forge-book-design-data")
@Transactional
public class ForgeBookDesignDataServlet extends HttpServlet {
  
	private static final long serialVersionUID = -5739692573670665390L;

	@Inject
	private Logger logger;

  @Inject
	private MaterialController materialController;
  
  @Inject
  private SystemSettingsController systemSettingsController;

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
    
    String secret = request.getParameter("secret");
    if (!StringUtils.equals(secret, systemSettingsController.getSetting(SystemSettingKey.PDF_SERVICE_CALLBACK_SECRET))) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    
    FileData data;
    try {
      data = materialController.getMaterialData(request.getContextPath(), null, bookDesign);
    } catch (GeneralSecurityException e) {
      logger.log(Level.SEVERE, "Could not retrieve material data because of a general security error", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
	
    if (data == null) {
  		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  		return;
    }
    
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
	
}
