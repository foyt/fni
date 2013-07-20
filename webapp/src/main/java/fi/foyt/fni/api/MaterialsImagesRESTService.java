package fi.foyt.fni.api;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;

@Path("/materials/images")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsImagesRESTService extends RESTService {
	
	@Inject
	private Logger logger;
	
	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private ImageDAO imageDAO;
	
	@GET
	@Path ("/{IMAGEID}")
  public Response image(
  		@PathParam ("IMAGEID") Long imageId,
  		@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		Image image = imageDAO.findById(imageId);
    if (image == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "IMAGEID")).build();
    }

		if (!materialPermissionController.isPublic(loggedUser, image)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}

			if (!materialPermissionController.hasAccessPermission(loggedUser, image)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
		
  	return createBinaryResponse(image.getData(), image.getContentType());
  }
	
	@GET
	@Path ("/{IMAGEID}/{SIZE}")
  public Response image(
  		@PathParam ("IMAGEID") Long imageId,
  		@PathParam ("SIZE") String sizeParam,
  		@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		Image image = imageDAO.findById(imageId);
    if (image == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "IMAGEID")).build();
    }

		if (!materialPermissionController.isPublic(loggedUser, image)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}

			if (!materialPermissionController.hasAccessPermission(loggedUser, image)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
		
    TypedData imageData = new TypedData(image.getData(), image.getContentType(), image.getModified());
    
    String[] sizes = sizeParam.split("x", 2);
    Integer width = NumberUtils.createInteger(sizes[0]);
    Integer height = NumberUtils.createInteger(sizes[1]);
    try {
      imageData = ImageUtils.resizeImage(imageData, width, height, null);
    } catch (IOException e) {
    	logger.log(Level.SEVERE, "Resizing failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.images.resizingFailed")).build();
    }
    
  	return createBinaryResponse(imageData.getData(), imageData.getContentType());
  }
}
