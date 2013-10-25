package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.scribe.model.Response;

import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Binary;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.ubuntuone.UbuntuOneManager;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/binary/*")
public class ForgeBinaryServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private MaterialController materialController;

	@Inject
	private UserController userController;

	@Inject
	private SessionController sessionController;

	@Inject
	private DropboxManager dropboxManager;

	@Inject
	private UbuntuOneManager ubuntuOneManager;
	
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
		
		FileData data = null;
		if (material instanceof Binary) {
			data = getBinaryMaterialData((Binary) material);
		} else if (material instanceof DropboxFile) {
			data = getDropboxMaterialData((DropboxFile) material);
		} if (material instanceof UbuntuOneFile) {
			data = getUbuntuOneMaterialData((UbuntuOneFile) material);
		}
		
		if (data == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		if (StringUtils.isNotBlank(data.getFileName())) {
			response.setHeader("content-disposition", "attachment; filename=" + data.getFileName());
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
	
	private FileData getBinaryMaterialData(Binary binary) {
		return new FileData(null, binary.getUrlName(), binary.getData(), binary.getContentType(), binary.getModified());
	}
	
	private FileData getDropboxMaterialData(DropboxFile dropboxFile) throws IOException {
		Response response = dropboxManager.getFileContent(sessionController.getLoggedUser(), dropboxFile);
		if (response.getCode() == 200) {
			byte[] data = null;
			
			InputStream inputStream = response.getStream();
			try {
				data = IOUtils.toByteArray(inputStream);
				return new FileData(null, dropboxFile.getUrlName(), data, dropboxFile.getMimeType(), dropboxFile.getModified());
			} finally {
			  inputStream.close();
			}
		}
		
		return null;
	}
	
	private FileData getUbuntuOneMaterialData(UbuntuOneFile ubuntuOneFile) throws IOException {
		Response response = ubuntuOneManager.getFileContent(sessionController.getLoggedUser(), ubuntuOneFile);
		if (response.getCode() == 200) {
			byte[] data = null;
			
			InputStream inputStream = response.getStream();
			try {
				data = IOUtils.toByteArray(inputStream);
				return new FileData(null, ubuntuOneFile.getUrlName(), data, ubuntuOneFile.getMimeType(), ubuntuOneFile.getModified());
			} finally {
			  inputStream.close();
			}
		}
		
		return null;
	}
	
}
