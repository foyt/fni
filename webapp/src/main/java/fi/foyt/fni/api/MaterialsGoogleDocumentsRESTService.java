package fi.foyt.fni.api;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.data.TypedData;

@Path("/materials/googleDocuments")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsGoogleDocumentsRESTService extends RESTService {

	@Inject
	private DriveManager driveManager;
	
	@Inject
	private MaterialController materialController;

	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	/**
   * Returns a Google document content. Document has to be public or user needs to have access to it.
   */
  @GET
  @Path ("/{GOOGLEDOCUMENTID}")
	public Response googleDocument(
			@PathParam ("GOOGLEDOCUMENTID") Long googleDocumentId,
			@Context HttpHeaders httpHeaders) {
 
  	Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
  	
		if (googleDocumentId == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "GOOGLEDOCUMENTID")).build();
    }
    
		GoogleDocument googleDocument = materialController.findGoogleDocumentById(googleDocumentId);
    if (googleDocument == null) {
    	return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
		
		if (!materialPermissionController.isPublic(loggedUser, googleDocument)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}

			if (!materialPermissionController.hasAccessPermission(loggedUser, googleDocument)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
		
  	try {
			Drive systemDrive = driveManager.getSystemDrive();
			File file = driveManager.getFile(systemDrive, googleDocument.getDocumentId());
			TypedData typedData = null;
			
			switch (googleDocument.getDocumentType()) {
				case DOCUMENT:
					typedData = driveManager.exportFile(systemDrive, file, "text/html");
				break;
				case DRAWING:
					typedData = driveManager.exportFile(systemDrive, file, "image/png");
				break;
				case PRESENTATION:
					typedData = driveManager.exportFile(systemDrive, file, "application/pdf");
				break;
				case SPREADSHEET:
					typedData = driveManager.exportSpreadsheet(systemDrive, file);
				break;
				case FOLDER:
				break;
				case FILE:
					typedData = driveManager.downloadFile(systemDrive, file);
				break;
			}

			if (typedData == null) {
	    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleDocumentsError")).build();
	    }
	    
	    return createBinaryResponse(typedData.getData(), typedData.getContentType());
			
		} catch (IOException | GeneralSecurityException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleCommunicationError")).build();
		}
  }

}
