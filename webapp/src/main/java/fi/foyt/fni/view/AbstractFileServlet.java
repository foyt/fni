package fi.foyt.fni.view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

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

	@Resource
	private UserTransaction userTransaction;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  	try {
    	// If transaction is not already active, we start it
   		boolean transactionActive = userTransaction.getStatus() == Status.STATUS_ACTIVE;
   		if (!transactionActive) {
   			userTransaction.begin();
   		}
  
  		try {
     		// Proceed with the request

  			super.service(req, resp);
    
    		// If transaction was started here, we commit the transaction
    		if (!transactionActive) {
    			userTransaction.commit();
    		} 
    		
    	} catch (Throwable t) {
    		// If exception was thrown and the transaction was started here, we rollback the transaction
    		if (!transactionActive) {
    			userTransaction.rollback();
    		}
    
    		// ... and throw an IOException
    		throw new IOException(t);
    	}  	
  	} catch (SystemException | NotSupportedException e) {
  		throw new ServletException(e);
		}
  }

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
