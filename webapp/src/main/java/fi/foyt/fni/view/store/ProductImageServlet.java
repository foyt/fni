package fi.foyt.fni.view.store;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;

@WebServlet(urlPatterns = "/store/productImages/*")
public class ProductImageServlet extends AbstractStoreFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;

	@Inject
	private ProductController productController;

	@Inject
	private SessionController sessionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ProductImageId could not be resolved, send 404
		Long productImageId = getPathId(request);
		if (productImageId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// ProductImage was not found, send 404
		ProductImage productImage = productController.findProductImageById(productImageId);
		if (productImage == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// TODO: If product is unpublished, only managers may view it
		// (productImage.getProduct().getPublished() != true)

		Integer width = NumberUtils.createInteger(request.getParameter("width"));
		Integer height = NumberUtils.createInteger(request.getParameter("height"));
		String eTag = createETag(productImage.getModified(), width, height);
		long lastModified = productImage.getModified().getTime();

		if (!isModifiedSince(request, lastModified, eTag)) {
			response.setHeader("ETag", eTag); // Required in 304.
			response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		TypedData data = new TypedData(productImage.getContent(), productImage.getContentType());

		if ((width != null) && (height != null)) {
			try {
				data = ImageUtils.resizeImage(data, width, height, null);
			} catch (IOException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resize image");
				return;
			}
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
			outputStream.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: Security

		List<UploadResultItem> resultItems = new ArrayList<>();
		User loggedUser = sessionController.getLoggedUser();

		try {
			Long productId = null;
			List<TypedData> images = new ArrayList<>();
			List<FileItem> items = getFileItems(request);
			
			for (FileItem item : items) {
				if ("productId".equals(item.getFieldName())) {
					productId = NumberUtils.createLong(item.getString());
				} else {
					images.add(new TypedData(item.get(), item.getContentType()));
				}
			}

			if (productId != null) {
				Product product = productController.findProductById(productId);
				if (product != null) {
					for (TypedData image : images) {
						ProductImage productImage = productController.createProductImage(product, image.getData(), image.getContentType(), loggedUser);
						String url = request.getContextPath() + "/fni/store/productImages/" + productImage.getId();
						String thumbnailUrl = url + "?width=128&height=128";
						resultItems.add(new UploadResultItem(productImage.getId().toString(), image.getData().length, url, thumbnailUrl, "N/A", "DELETE"));
					}
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "productId parameter is invalid");
					return;
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "productId parameter is required");
				return;
			}

		} catch (FileUploadException e) {
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
			writer.close();
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
}
