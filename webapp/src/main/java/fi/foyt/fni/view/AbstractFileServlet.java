package fi.foyt.fni.view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractFileServlet extends HttpServlet {

	private static final long serialVersionUID = 2682138379342291553L;

  protected static final long DEFAULT_EXPIRE_TIME = 1000 * 60 * 60;

	protected Long getPathId(HttpServletRequest req) {
		String pathInfo = req.getPathInfo();
		int lastSlash = pathInfo.lastIndexOf('/');
		String lastBlock = pathInfo.substring(lastSlash + 1);
		if (StringUtils.isNumeric(lastBlock)) {
			return NumberUtils.createLong(lastBlock);
		}

		return null;
	}

	protected boolean isModifiedSince(HttpServletRequest request, Long lastModified, String eTag) throws IOException {
		// If 'If-None-Match' header contains * or matches the ETag send 304
		String ifNoneMatch = request.getHeader("If-None-Match");
		if ("*".equals(ifNoneMatch) || eTag.equals(ifNoneMatch)) {
			return false;
		}

		// If 'If-Modified-Since' header is greater than LastModified send 304.
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifNoneMatch == null) && (ifModifiedSince != -1) && ((ifModifiedSince + 1000) > lastModified)) {
			return false;
		}
		
		return true;
	}
	
	protected List<FileItem> getFileItems(HttpServletRequest request) throws FileUploadException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		((DiskFileItemFactory) factory).setRepository(repository);
		ServletFileUpload upload = new ServletFileUpload(factory);
		return upload.parseRequest(request);
	};
}
