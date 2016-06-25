package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.PublicationImageCache;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(
  urlPatterns = { "/gamelibrary/publicationImages/*", "/store/productImages/*" }, 
  name = "gamelibrary-publicationimage"
)
@Transactional
public class PublicationImageServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;
	
	@Inject
	private Logger logger;

	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;

  @Inject
	private PublicationImageCache publicationImageCache;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// PublicationImageId could not be resolved, send 404
		Long publicationImageId = getPathId(request);
		if (publicationImageId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// PublicationImage was not found, send 404
		PublicationImage publicationImage = publicationController.findPublicationImageById(publicationImageId);
		if (publicationImage == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if (!publicationImage.getPublication().getPublished()) {
			if (!sessionController.isLoggedIn()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

      if (!publicationImage.getCreator().getId().equals(sessionController.getLoggedUserId())) {
  			if (!sessionController.hasLoggedUserPermission(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)) {
  				response.sendError(HttpServletResponse.SC_FORBIDDEN);
  				return;
  			}
      }
		}

		Integer width = NumberUtils.createInteger(request.getParameter("width"));
		Integer height = NumberUtils.createInteger(request.getParameter("height"));
		
		String eTag = createETag(publicationImage.getModified(), width, height);
		long lastModified = publicationImage.getModified().getTime();

		if (!isModifiedSince(request, lastModified, eTag)) {
			response.setHeader("ETag", eTag); // Required in 304.
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

    TypedData data = publicationImageCache.get(publicationImageId, width, height);
    if (data == null) {
  		data = new TypedData(publicationImage.getContent(), publicationImage.getContentType());
  
  		if ((width != null) || (height != null)) {
  			try {
  				data = ImageUtils.resizeImage(data, width != null ? width : -1, height != null ? height : -1, null);
  			} catch (IOException e) {
  	      logger.log(Level.SEVERE, "Failed to resize image", e);
  				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resize image");
  				return;
  			}
  		}
  		
  		publicationImageCache.put(publicationImageId, width, height, data);
    }
  
		response.setContentType(data.getContentType());
		response.setHeader("ETag", eTag);
		response.setDateHeader("Last-Modified", lastModified);
		response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(data.getData());
		} finally {
			outputStream.flush();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User loggedUser = sessionController.getLoggedUser();
		Long publicationId = getPathId(request);
		if (publicationId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Publication publication = publicationController.findPublicationById(publicationId);
		if (publication == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if (!publication.getPublished()) {
			if (!sessionController.isLoggedIn()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			if (!sessionController.hasLoggedUserPermission(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
		
		Mode mode = Mode.APPEND;
		String modeParameter = request.getParameter("mode");
		if (StringUtils.isNotBlank(modeParameter)) {
			mode = Mode.valueOf(modeParameter);
		}
		
		List<UploadResultItem> resultItems = new ArrayList<>();

		try {
			List<TypedData> images = new ArrayList<>();
			List<FileItem> items = getFileItems(request);

			for (FileItem item : items) {
				if (!item.isFormField()) {
					images.add(new TypedData(item.get(), item.getContentType()));
				}
			}
			
			if (mode == Mode.REPLACE) {
				publicationController.updatePublicationDefaultImage(publication, null);
				List<PublicationImage> oldImages = publicationController.listPublicationImagesByPublication(publication);
				for (PublicationImage oldImage : oldImages) {
          publicationImageCache.remove(oldImage.getId());
					publicationController.deletePublicationImage(oldImage);
				}
			}

			for (TypedData image : images) {
				PublicationImage publicationImage = publicationController.createPublicationImage(publication, image.getData(), image.getContentType(), loggedUser);
				if (publication.getDefaultImage() == null) {
					// If publication does not yet have a default image we update it to uploaded image
					publicationController.updatePublicationDefaultImage(publication, publicationImage);
				}

				String url = request.getContextPath() + "/gamelibrary/publicationImages/" + publicationImage.getId();
				String thumbnailUrl = url + "?width=128&height=128";
				resultItems.add(new UploadResultItem(publicationImage.getId().toString(), image.getData().length, url, thumbnailUrl, "N/A", "DELETE"));
			}

		} catch (FileUploadException e) {
      logger.log(Level.SEVERE, "File uploading failed", e);
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

	private String createETag(Date modified, Integer width, Integer height) {
		StringBuilder eTagBuilder = new StringBuilder();

		eTagBuilder.append("W/").append(modified.getTime());

		if (width != null) {
			eTagBuilder.append('-').append(width);
		}

		if (height != null) {
			eTagBuilder.append('-').append(height);
		}

		return eTagBuilder.toString();
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
	
	private enum Mode {
		APPEND,
		REPLACE
	}
}
