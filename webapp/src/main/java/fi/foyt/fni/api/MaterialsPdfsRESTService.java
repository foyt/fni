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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Path("/materials/pdfs")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsPdfsRESTService extends RESTService {
	
	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	@DAO
	private PdfDAO pdfDAO;
	
	/**
   * Returns pdf. Pdf has to be either public or user has to have access to it
   **/
	@Path ("/{PDFID}")
  @GET
  public Response represent(
    @PathParam ("PDFID") Long pdfId,
    @Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

    if (pdfId == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "PDFID")).build();
    }
    
    Pdf pdf = pdfDAO.findById(pdfId);
    if (pdf == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
    
    if (!materialPermissionController.isPublic(loggedUser, pdf)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}

			if (!materialPermissionController.hasAccessPermission(loggedUser, pdf)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
		
    String urlName = pdf.getUrlName();
    String extension = FilenameUtils.getExtension(urlName);
    if (!"pdf".equals(extension)) {
    	if (StringUtils.isBlank(extension)) {
    		urlName += ".pdf";
    	} else {
    		urlName = urlName.substring(0, urlName.length() - (extension.length() + 1)) + ".pdf";
    	}
    }
    
  	return createBinaryResponse(pdf.getData(), pdf.getContentType(), urlName);
  }

}
