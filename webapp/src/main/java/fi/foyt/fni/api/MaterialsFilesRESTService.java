package fi.foyt.fni.api;

import java.util.Locale;

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

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.FileDAO;
import fi.foyt.fni.persistence.model.materials.File;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Path("/materials/files")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsFilesRESTService extends RESTService {

	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private FileDAO fileDAO;
	
	/**
   * Returns a file. File has to be either public or user has to have access to it
   **/
	@Path ("/{FILEID}")
  @GET
  public Response represent(
    @PathParam ("FILEID") Long fileId,
    @Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (fileId == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "FILEID")).build();
    }
    
    File file = fileDAO.findById(fileId);
    if (file == null) {
    	return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }		
    
    if (!materialPermissionController.isPublic(loggedUser, file)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}

			if (!materialPermissionController.hasAccessPermission(loggedUser, file)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
    
  	return createBinaryResponse(file.getData(), file.getContentType(), file.getUrlName());
  }

}
