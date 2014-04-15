package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.materials.FolderController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/forge/upload/", name = "forge-upload" )
public class ForgeUploadServlet extends AbstractFileServlet {

	private static final long serialVersionUID = -4376406243780463521L;

  @Inject
	private MaterialController materialController;

  @Inject
	private FolderController folderController;

	@Inject
	private MaterialPermissionController materialPermissionController;
	
	@Inject
	private SessionController sessionController;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!sessionController.isLoggedIn()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		Folder parentFolder = null;
		String parentFolderIdParameter = request.getParameter("parentFolderId");
		if (StringUtils.isNotBlank(parentFolderIdParameter)) {
		  Long parentFolderId = NumberUtils.createLong(parentFolderIdParameter);
		  if (parentFolderId != null) {
		    parentFolder = folderController.findFolderById(parentFolderId);
	      if (parentFolder != null) {
	        if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
	          response.sendError(HttpServletResponse.SC_FORBIDDEN);
	          return;
	        }
	      } else {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND);
	        return;
	      }
		  } else {
	      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	      return;
		  }
		}
		
		User loggedUser = sessionController.getLoggedUser();
		List<UploadResultItem> resultItems = new ArrayList<>();

		try {
			List<FileData> files = new ArrayList<>();
			List<FileItem> fileItems = getFileItems(request);

			for (FileItem fileItem : fileItems) {
				if (!fileItem.isFormField()) {
					files.add(new FileData(fileItem.getFieldName(), fileItem.getName(), fileItem.get(), fileItem.getContentType(), null));
				}
			}
			
			for (FileData file : files) {
				Material material = materialController.createMaterial(parentFolder, loggedUser, file);
				resultItems.add(new UploadResultItem(material.getId().toString(), file.getData().length, "N/A", "N/A", "N/A", "DELETE"));
			}

		} catch (FileUploadException | MimeTypeParseException | GeneralSecurityException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		response.setContentType("application/json");

		PrintWriter writer = response.getWriter();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, List<UploadResultItem>> result = new HashMap<>();
			result.put("files", resultItems);
			mapper.writeValue(writer, result);
		} finally {
			writer.flush();
		}
	}
	
	public class UploadResultItem {

		public UploadResultItem(String name, int size, String url, String thumbnailUrl, String deleteUrl, String deleteType) {
			this.name = name;
			this.size = size;
			this.url = url;
			this.thumbnailUrl = thumbnailUrl;
			this.deleteUrl = deleteUrl;
			this.deleteType = deleteType;
		}

		public String getName() {
			return name;
		}

		public int getSize() {
			return size;
		}

		public String getUrl() {
			return url;
		}

		public String getThumbnailUrl() {
			return thumbnailUrl;
		}

		public String getDeleteUrl() {
			return deleteUrl;
		}

		public String getDeleteType() {
			return deleteType;
		}

		private String name;
		private int size;
		private String url;
		private String thumbnailUrl;
		private String deleteUrl;
		private String deleteType;
	}
}
