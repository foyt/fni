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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneFileDAO;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.ubuntuone.UbuntuOneManager;
import fi.foyt.fni.utils.streams.StreamUtils;

@Path("/materials/ubuntuOne")
@RequestScoped
@Stateful
public class MaterialsUbuntuOneRESTService extends RESTService {
	
  @Inject
  private Logger logger;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private UbuntuOneFileDAO ubuntuOneFileDAO;
  
	@Inject
	private UbuntuOneManager ubuntuOneManager;
	
	/**
   * Returns a Google document content. Document has to be public or user needs to have access to it.
   */
  @GET
  @Path ("/{UBUNTUONEFILEID}")
	public Response ubuntuOneFile(
			@PathParam ("UBUNTUONEFILEID") Long ubuntuOneFileId,
			@Context HttpHeaders httpHeaders) {

    Locale browserLocale = getBrowserLocale(httpHeaders);
    User loggedUser = getLoggedUser(httpHeaders);
		
		if (ubuntuOneFileId == null) {
		  return Response.status(Status.NOT_FOUND).build();
		}
		
		UbuntuOneFile ubuntuOneFile = ubuntuOneFileDAO.findById(ubuntuOneFileId);
    if (ubuntuOneFile == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.isPublic(loggedUser, ubuntuOneFile)) {
      if (!hasRole(loggedUser, UserRole.GUEST)) {
        return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
      }

      if (!materialPermissionController.hasAccessPermission(loggedUser, ubuntuOneFile)) {
        return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
      }
    }
    
    org.scribe.model.Response response;
    try {
      response = ubuntuOneManager.getFileContent(loggedUser, ubuntuOneFile);
    } catch (IOException e1) {
      logger.log(Level.SEVERE, "Failed to fetch file from Ubuntu One", e1);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
    
    if (response.getCode() == 200) {
      byte[] data;
      try {
        data = StreamUtils.getInputStreamAsBytes(response.getStream());
        return createBinaryResponse(data, ubuntuOneFile.getMimeType());
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to fetch file from Ubuntu One", e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
      }
    } else {
      logger.log(Level.SEVERE, response.getBody());
      return Response.status(response.getCode()).build();
    }
  }
}
