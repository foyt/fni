package fi.foyt.fni.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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

import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.gdocs.GoogleDocumentsClient;
import fi.foyt.fni.utils.gdocs.GoogleDocumentsUtils;
import fi.foyt.fni.utils.gdocs.GooleDocumentsException;
import fi.foyt.fni.utils.html.CSSUtils;

@Path("/materials/googleDocuments")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsGoogleDocumentsRESTService extends RESTService {

	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	@DAO
	private GoogleDocumentDAO googleDocumentDAO;
	
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
    
    GoogleDocument googleDocument = googleDocumentDAO.findById(googleDocumentId);
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
    
    TypedData output = null;

    try {
    	String adminUsername = systemSettingsController.getSetting("materials.googleDocs.username");
			String adminPassword = systemSettingsController.getSetting("materials.googleDocs.password");
			GoogleDocumentsClient googleDocumentsClient = GoogleDocumentsUtils.createClient(adminUsername, adminPassword);

			switch (googleDocument.getDocumentType()) {
        case DOCUMENT:
          TypedData originalData = downloadDocument(googleDocumentsClient, googleDocument, "html");

          // TODO: Hack (makes documents look more google like)

          CSSStyleSheet styleSheet = CSSUtils.createStyleSheet();
          CSSStyleRule htmlRule = CSSUtils.createStyleRule("html");
          CSSUtils.addPropery(htmlRule, "background-color", "#EBEBEB", false);
          CSSUtils.addRule(styleSheet, htmlRule);
          
          CSSStyleRule bodyRule = CSSUtils.createStyleRule("body");
          CSSUtils.addPropery(bodyRule, "margin-left", "auto");
          CSSUtils.addPropery(bodyRule, "margin-right", "auto");
          CSSUtils.addPropery(bodyRule, "border", "1px solid #CCCCCC");
          CSSUtils.addRule(styleSheet, bodyRule);
          
          String cssText = CSSUtils.getStyleSheetAsString(styleSheet);
          
          output = addHtmlExtraStyle(originalData.getData(), cssText);
        break;
        case DRAWING:
          output = downloadDrawing(googleDocumentsClient, googleDocument, "png");
        break;
        case SPREADSHEET:
          output = downloadSpreadsheet(googleDocumentsClient, googleDocument, "html");
        break;
        case PRESENTATION:
          output = downloadPresentation(googleDocumentsClient, googleDocument, "png");
        break;
        default:
        break;
      }

    } catch (AuthenticationException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleAuthenticationError")).build();
    } catch (IOException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleCommunicationError")).build();
    } catch (ServiceException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleRequestError")).build();
    } catch (GooleDocumentsException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleDocumentsError")).build();
    }

    if (output == null) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleDocumentsError")).build();
    }
    
    return createBinaryResponse(output.getData(), output.getContentType());
  }
  
  private TypedData addHtmlExtraStyle(byte[] data, String cssText) throws UnsupportedEncodingException {
    String extraCss = "<style>" + cssText + "</style>";
    
    String htmlData = new String(data, "UTF-8");
    
    int headIndex = htmlData.indexOf("</head>");
    StringBuilder outputBuilder = new StringBuilder();
    outputBuilder.append(htmlData.substring(0, headIndex));
    outputBuilder.append(extraCss);
    outputBuilder.append(htmlData.substring(headIndex));

    return new TypedData(outputBuilder.toString().getBytes("UTF-8"), "text/html");    
  }
  
  /**
   * Downloads a Google document in specific format. Document has to be public or user needs to have access to it.
   */
  @GET
  @Path ("/{GOOGLEDOCUMENTID}/download/{FORMAT}")
  public Response download(
  		@PathParam ("GOOGLEDOCUMENTID") Long googleDocumentId,
  		@PathParam ("FORMAT") String format,
  		@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

    if (googleDocumentId == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "GOOGLEDOCUMENTID")).build();
    }
    
    GoogleDocument googleDocument = googleDocumentDAO.findById(googleDocumentId);
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
		
    if (format == null) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "FORMAT")).build();
    }
    
    TypedData output = null;
    try {
    	String adminUsername = systemSettingsController.getSetting("materials.googleDocs.username");
			String adminPassword = systemSettingsController.getSetting("materials.googleDocs.password");
			GoogleDocumentsClient googleDocumentsClient = GoogleDocumentsUtils.createClient(adminUsername, adminPassword);

      switch (googleDocument.getDocumentType()) {
        case DOCUMENT:
          if (isSupportedFormat(new String[] { "doc", "txt", "odt", "png", "pdf", "rtf", "html"}, format)) {
            output = downloadDocument(googleDocumentsClient, googleDocument, format);
          } else {
          	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.unsupportedDocumentFormat", format)).build();
          }
        break;
        case DRAWING:
          if (isSupportedFormat(new String[] { "jpeg", "pdf", "png", "svg" }, format)) {
            output = downloadDrawing(googleDocumentsClient, googleDocument, format);
          } else {
          	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.unsupportedDrawingFormat", format)).build();
          }
        break;
        case SPREADSHEET:
          if (isSupportedFormat(new String[] { "ods", "pdf", "xls", "csv", "html", "tsv" }, format)) {
            output = downloadSpreadsheet(googleDocumentsClient, googleDocument, format);
          } else {
          	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.unsupportedSpreadsheetFormat", format)).build();
          }
        break;
        case PRESENTATION:
          if (isSupportedFormat(new String[] { "pdf", "ppt", "png", "swf", "txt" }, format)) {
            output = downloadPresentation(googleDocumentsClient, googleDocument, format);
          } else {
          	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.unsupportedPresentationFormat", format)).build();
          }
        break;
        default:
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleDocumentsError")).build();
      }

    } catch (AuthenticationException e) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleAuthenticationError")).build();
    } catch (IOException e) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleCommunicationError")).build();
    } catch (ServiceException e) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleRequestError")).build();
    } catch (GooleDocumentsException e) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleDocumentsError")).build();
    }
    
    if (output == null) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleDocumentsError")).build();
    }
    
    return createBinaryResponse(output.getData(), output.getContentType(), googleDocument.getUrlName() + "." + format);
  }
  
  private TypedData downloadSpreadsheet(GoogleDocumentsClient googleDocumentsClient, GoogleDocument googleDocument, String format) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    googleDocumentsClient.downloadSpreadsheet(googleDocument.getDocumentId(), outputStream, format);

    if ("html".equals(format)) {
      // TODO: Hack...
      return addHtmlExtraStyle(outputStream.toByteArray(), "table td:first-child { display: none; }");
    }
    
    return new TypedData(outputStream.toByteArray(), getMimeTypeForFormat(format));
  }

  private TypedData downloadDrawing(GoogleDocumentsClient googleDocumentsClient, GoogleDocument googleDocument, String format) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    googleDocumentsClient.downloadDrawing(googleDocument.getDocumentId(), outputStream, format);
    return new TypedData(outputStream.toByteArray(), getMimeTypeForFormat(format));
  }

  private TypedData downloadPresentation(GoogleDocumentsClient googleDocumentsClient, GoogleDocument googleDocument, String format) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    googleDocumentsClient.downloadPresentation(googleDocument.getDocumentId(), outputStream, format);
    return new TypedData(outputStream.toByteArray(), getMimeTypeForFormat(format));
  }

  private TypedData downloadDocument(GoogleDocumentsClient googleDocumentsClient, GoogleDocument googleDocument, String format) throws MalformedURLException, IOException, ServiceException, GooleDocumentsException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    googleDocumentsClient.downloadDocument(googleDocument.getDocumentId(), outputStream, format);
    return new TypedData(outputStream.toByteArray(), getMimeTypeForFormat(format));
  }
  
  private String getMimeTypeForFormat(String format) {
    com.google.gdata.data.docs.DocumentListEntry.MediaType mediaType = com.google.gdata.data.docs.DocumentListEntry.MediaType.valueOf(format.toUpperCase());
    if (mediaType != null)
      return mediaType.getMimeType();
    else
      return null;
  }

  private boolean isSupportedFormat(String[] supportedFormats, String format) {
    for (String supportedFormat : supportedFormats) {
      if (supportedFormat.equals(format))
        return true;
    }
    
    return false;
  }
}
