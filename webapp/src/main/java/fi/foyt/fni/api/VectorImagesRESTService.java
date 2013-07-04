package fi.foyt.fni.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import fi.foyt.fni.api.beans.CompactVectorImageBean;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageRevisionDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.utils.compression.CompressionUtils;
import fi.foyt.fni.utils.diff.DiffUtils;
import fi.foyt.fni.utils.html.HtmlUtils;
import fi.foyt.fni.utils.language.GuessedLanguage;
import fi.foyt.fni.utils.language.LanguageUtils;

@Path("/materials/vectorImages")
@RequestScoped
@Stateful
@Produces ("application/json")
public class VectorImagesRESTService extends RESTService {
	
	@Inject
	private Logger logger;
	
	@Inject
  private MaterialController materialController;
	
  @Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	@DAO
	private FolderDAO folderDAO;
	
	@Inject
	@DAO
	private VectorImageDAO vectorImageDAO;
	
	@Inject
	@DAO
	private LanguageDAO languageDAO;
	
	@Inject
	@DAO
	private VectorImageRevisionDAO vectorImageRevisionDAO;

	/**
   * Creates a new vector image. User needs to be logged in and needs to have modification permission into the parent folder
   */
	@POST
  @PUT
  @Path ("/createVectorImage")
  public Response create(
  		@FormParam ("title") String title, 
  		@FormParam ("data") String data,
  		@FormParam ("parentFolderId") String parentFolderIdString,
  		@FormParam ("language") String languageParam,
  		@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Long parentFolderId = "HOME".equals(parentFolderIdString) ? null : NumberUtils.createLong(parentFolderIdString);

		Folder parentFolder = null;
		if (parentFolderId != null) {
			parentFolder = folderDAO.findById(parentFolderId);
			if (!materialPermissionController.hasModifyPermission(loggedUser, parentFolder)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}

		if (StringUtils.isBlank(title)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "title")).build();
		}

		Language language = StringUtils.isBlank(languageParam) ? null : languageDAO.findByIso2(languageParam);
		String urlName = materialController.getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
		VectorImage vectorImage = vectorImageDAO.create(loggedUser, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
		
		return Response.ok(new ApiResult<>(CompactVectorImageBean.fromEntity(vectorImage))).build();
  }
	
	/**
   * Updates a vector image. User needs to be logged in and have a modification permission to document
   */
	@POST
  @PUT
  @Path ("/{VECTORIMAGEID}/updateVectorImage")
  public Response updateVectorImage(
  		@PathParam ("VECTORIMAGEID") Long vectorImageId,
  		@FormParam ("title") String title,
  		@FormParam ("data") String data,
  		@FormParam ("languageParam") String languageParam,
  		@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

    VectorImage vectorImage = vectorImageDAO.findById(vectorImageId);

		if (!materialPermissionController.hasModifyPermission(loggedUser, vectorImage)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
    Language language = StringUtils.isBlank(languageParam) ? null : languageDAO.findByIso2(languageParam);
		if (language == null) {
			List<GuessedLanguage> guessedLanguages;
      try {
	      guessedLanguages = LanguageUtils.getGuessedLanguages(data, 0.2);
  			if (guessedLanguages.size() > 0) {
  				String languageCode = guessedLanguages.get(0).getLanguageCode();
  				language = languageDAO.findByIso2(languageCode);
  			}
      } catch (IOException e) {
      	// It's really not very serious if language detection fails.
      	logger.log(Level.WARNING, "Language detection failed", e);
      }
		}
    
		String oldData = vectorImage.getData();
		if (!StringUtils.isEmpty(oldData) && !data.equals(oldData)) {
			Long lastRevision = vectorImageRevisionDAO.maxRevisionByVectorImage(vectorImage);
			if (lastRevision == null)
				lastRevision = 0l;

			boolean compressed = false;
			String patchData = DiffUtils.makePatch(data, oldData);
			byte[] patchBytes;
      
			try {
	      patchBytes = patchData.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
  			logger.log(Level.SEVERE, "UTF-8 not supported", e);
  			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
      }

      try {
      	patchBytes = CompressionUtils.compressBzip2Array(patchBytes);
	      compressed = true;
      } catch (IOException e) {
      	// If revision compression fails, we log it and save the revision as uncompressed
      	logger.log(Level.SEVERE, "revision compression failed", e);
      }

      vectorImageRevisionDAO.create(vectorImage, lastRevision + 1, new Date(), compressed, false, patchBytes, null, null);
		}

		vectorImageDAO.updateTitle(vectorImage, loggedUser, title);
		vectorImageDAO.updateData(vectorImage, loggedUser, data);
    
    return Response.ok(new ApiResult<>(CompactVectorImageBean.fromEntity(vectorImage))).build();
  }
	
  /**
   * Returns a vector image content. Image has to be public or user needs to have access to it.
   */
	@GET
	@Path ("/{VECTORIMAGEID}")
  public Response image(
  		@PathParam ("VECTORIMAGEID") Long vectorImageId,
  		@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		VectorImage vectorImage = vectorImageDAO.findById(vectorImageId);
	
		if (!materialPermissionController.isPublic(loggedUser, vectorImage)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
			
			if (!materialPermissionController.hasAccessPermission(loggedUser, vectorImage)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
		
  	try {
	    return createBinaryResponse(vectorImage.getData().getBytes("UTF-8"), "image/svg+xml");
    } catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "UTF-8 not supported", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    }
  }
}
