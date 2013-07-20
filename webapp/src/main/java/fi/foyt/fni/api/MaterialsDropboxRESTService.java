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

import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.DropboxFileDAO;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.utils.streams.StreamUtils;

@Path("/materials/dropbox")
@RequestScoped
@Stateful
public class MaterialsDropboxRESTService extends RESTService {
	
  @Inject
  private Logger logger;
  
  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private DropboxFileDAO dropboxFileDAO;
  
	@Inject
	private DropboxManager dropboxManager;
	
	/**
   * Returns a Google document content. Document has to be public or user needs to have access to it.
   */
  @GET
  @Path ("/{DROPBOXFILEID}")
	public Response dropboxFile(
			@PathParam ("DROPBOXFILEID") Long dropboxFileId,
			@Context HttpHeaders httpHeaders) {

		User loggedUser = getLoggedUser(httpHeaders);
	   Locale browserLocale = getBrowserLocale(httpHeaders);
	
		if (dropboxFileId == null) {
		  return Response.status(Status.NOT_FOUND).build();
		}
		
		DropboxFile dropboxFile = dropboxFileDAO.findById(dropboxFileId);
    if (dropboxFile == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.isPublic(loggedUser, dropboxFile)) {
      if (!hasRole(loggedUser, UserRole.GUEST)) {
        return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
      }

      if (!materialPermissionController.hasAccessPermission(loggedUser, dropboxFile)) {
        return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
      }
    }
		
    org.scribe.model.Response response;
    try {
      response = dropboxManager.getFileContent(loggedUser, dropboxFile);
    } catch (IOException e1) {
      logger.log(Level.SEVERE, "Failed to fetch file from Dropbox", e1);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
    
    if (response.getCode() == 200) {
      byte[] data;
      try {
        data = StreamUtils.getInputStreamAsBytes(response.getStream());
        return createBinaryResponse(data, dropboxFile.getMimeType());
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to fetch file from Dropbox", e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
      }
    } else {
      logger.log(Level.SEVERE, response.getBody());
      return Response.status(response.getCode()).build();
    }
  }
}
