package fi.foyt.fni.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.scribe.model.Response;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.materials.GoogleDriveMaterialController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Binary;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.File;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.ubuntuone.UbuntuOneManager;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.servlet.RequestUtils;

@WebServlet ( urlPatterns = "/materials/*")
public class MaterialsServlet extends AbstractTransactionedServlet {
  
  private static final String DOCUMENT_TEMPLATE = "<!DOCTYPE HTML><html><head><meta charset=\"UTF-8\"><title>{0}</title><link rel=\"StyleSheet\" href=\"{1}\"/></head><body>{2}</body></html>";
	
	private static final long serialVersionUID = -5739692573670665390L;

	@Inject
	private Logger logger;
	
  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
	private MaterialController materialController;
  
  @Inject
  private GoogleDriveMaterialController googleDriveMaterialController;

	@Inject
	private DropboxManager dropboxManager;

	@Inject
	private UbuntuOneManager ubuntuOneManager;

	@Inject
	private DriveManager driveManager;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Material material = materialController.findMaterialByCompletePath(RequestUtils.stripCtxPath(request.getContextPath(), request.getRequestURI()));
		if (material == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
    }

    User loggedUser = sessionController.getLoggedUser();
    if (!(materialPermissionController.isPublic(loggedUser, material) || materialPermissionController.hasAccessPermission(loggedUser, material))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
    }

    FileData data = null;
    
		switch (material.getType()) {
			case IMAGE:
				data = getBinaryMaterialData((Image) material);
			break;
			case DOCUMENT:
				data = getDocumentData(request.getContextPath(), (Document) material);
			break;
			case VECTOR_IMAGE:
				data = getVectorImageData((VectorImage) material);
			break;
			case PDF:
				data = getBinaryMaterialData((Pdf) material);
			break;
			case FILE:
				data = getBinaryMaterialData((File) material);
			break;
			case GOOGLE_DOCUMENT:
        try {
          TypedData typedData = googleDriveMaterialController.getGoogleDocumentData((GoogleDocument) material);
          data = new FileData(null, material.getUrlName(), typedData.getData(), typedData.getContentType(), typedData.getModified());
        } catch (GeneralSecurityException e) {
          logger.log(Level.SEVERE, "Could not serve Google Drive File", e);
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          return;
        }
			break;
			case DROPBOX_FILE:
				data = getDropboxMaterialData((DropboxFile) material);
			break;
			case UBUNTU_ONE_FILE:
				data = getUbuntuOneMaterialData((UbuntuOneFile) material);
			break;
			case BINARY:
				data = getBinaryMaterialData((Binary) material);
			case DROPBOX_FOLDER:
			case DROPBOX_ROOT_FOLDER:
			case FOLDER:
			case UBUNTU_ONE_FOLDER:
			case UBUNTU_ONE_ROOT_FOLDER:
				data = null;
  		break;
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
		}
	}

	private FileData getDocumentData(String contextPath, Document document) throws UnsupportedEncodingException {
	  String bodyContent = document.getData();
	  String title = document.getTitle();
	  String styleSheet = contextPath + "/uresources/material-document-style.css";
	  String htmlContent = MessageFormat.format(DOCUMENT_TEMPLATE, title, styleSheet, bodyContent);
		return new FileData(null, null, htmlContent.getBytes("UTF-8"), "text/html", document.getModified());
	}

	private FileData getVectorImageData(VectorImage vectorImage) throws UnsupportedEncodingException {
		return new FileData(null, null, vectorImage.getData().getBytes("UTF-8"), "image/svg+xml", vectorImage.getModified());
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
				String fileName = null;
				if (!dropboxFile.getMimeType().startsWith("image/")&&!dropboxFile.getMimeType().equals("text/html")) {
					fileName = dropboxFile.getUrlName();
				}
				
				return new FileData(null, fileName, data, dropboxFile.getMimeType(), dropboxFile.getModified());
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
				String fileName = null;
				if (!ubuntuOneFile.getMimeType().startsWith("image/")&&!ubuntuOneFile.getMimeType().equals("text/html")) {
					fileName = ubuntuOneFile.getUrlName();
				}
				
				return new FileData(null, fileName, data, ubuntuOneFile.getMimeType(), ubuntuOneFile.getModified());
			} finally {
			  inputStream.close();
			}
		}
		
		return null;
	}
	
}
