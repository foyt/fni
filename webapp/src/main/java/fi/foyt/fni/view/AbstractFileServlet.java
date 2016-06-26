package fi.foyt.fni.view;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractFileServlet extends AbstractServlet {

	private static final long serialVersionUID = 2682138379342291553L;

	protected static final long DEFAULT_EXPIRE_TIME = 1000L * 60 * 60;
	
	protected String getPathLastBlock(HttpServletRequest req) {
    String pathInfo = req.getPathInfo();
    int lastSlash = pathInfo.lastIndexOf('/');
    return pathInfo.substring(lastSlash + 1);
	}

	protected Long getPathId(HttpServletRequest req) {
	  String lastBlock = getPathLastBlock(req);
		if (StringUtils.isNumeric(lastBlock)) {
			return NumberUtils.createLong(lastBlock);
		}

		return null;
	}

	protected List<FileItem> getFileItems(HttpServletRequest request) throws FileUploadException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		((DiskFileItemFactory) factory).setRepository(repository);
		ServletFileUpload upload = new ServletFileUpload(factory);
		return upload.parseRequest(request);
	}
}
