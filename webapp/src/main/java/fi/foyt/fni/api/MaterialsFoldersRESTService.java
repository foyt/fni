package fi.foyt.fni.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.api.beans.CompactFolderBean;
import fi.foyt.fni.api.beans.CompactMaterialBean;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Path("/materials/folders")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsFoldersRESTService extends RESTService {
	
	@Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private MaterialController materialController;
  
	@Inject
	private FolderDAO folderDAO;

  @Inject
  private MaterialDAO materialDAO;
  
	/**
   * Returns list of folders inside a parent folder. User has to be logged in and have at least access permission into the parent folder
   **/
	@GET
	@Path ("/{PARENTFOLDERID}/listFolders")
	public Response listFolders(
			@PathParam ("PARENTFOLDERID") String parentFolderAttribute,
			@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Folder parentFolder;
		
		if ("HOME".equals(parentFolderAttribute)) {
			parentFolder = null;
		} else {
  		Long parentFolderId = NumberUtils.createLong(parentFolderAttribute);
	  	parentFolder = folderDAO.findById(parentFolderId);
		}
		
		if (parentFolder != null) {
	    if (!materialPermissionController.hasAccessPermission(loggedUser, parentFolder)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
	    }
		}
		
		List<Material> folders = materialController.listMaterialsByFolderAndTypes(loggedUser, parentFolder, Arrays.asList(MaterialType.FOLDER));
		Map<String, List<CompactMaterialBean>> result = new HashMap<String, List<CompactMaterialBean>>();
		result.put("folders", CompactMaterialBean.fromMaterialEntities(folders));
		
		return Response.ok(new ApiResult<>(result)).build();
	}
	
  /**
   * Creates new Folder. User has to be logged in and have modification permission into the parent folder
   **/
	@POST
	@Path ("/{PARENTFOLDERID}/createFolder")
	public Response createFolder(
			@PathParam ("PARENTFOLDERID") String parentFolderAttribute,
			@FormParam ("title") String title, 
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

		Folder parentFolder;
		Date now = new Date();
		
		if ("HOME".equals(parentFolderAttribute)) {
			parentFolder = null;
		} else {
  		Long parentFolderId = NumberUtils.createLong(parentFolderAttribute);
	  	parentFolder = folderDAO.findById(parentFolderId);
		}
		
		if (parentFolder != null) {
	    if (!materialPermissionController.hasModifyPermission(loggedUser, parentFolder)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
	    }
		}
		
		String urlName = materialController.getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
		
		Folder folder = folderDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE);

		return Response.ok(new ApiResult<>(CompactFolderBean.fromEntity(folder))).build();
	}
	
  /**
   * Updates a folder. User has to be logged in and have modification permission into the folder
   **/
	@POST
	@PUT
	@Path ("/{FOLDERID}/updateFolder")
	public Response updateFolder(
			@PathParam ("FOLDERID") Long folderId,
			@FormParam ("title") String title, 
			@Context HttpHeaders httpHeaders
			) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		if (folderId == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "FOLDERID")).build();
    }
		
		Folder folder = folderDAO.findById(folderId);
		if (folder == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
		
    if (!materialPermissionController.hasModifyPermission(loggedUser, folder)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }

		String urlName = materialController.getUniqueMaterialUrlName(folder.getCreator(), folder.getParentFolder(), folder, title);
		
		materialDAO.updateTitle(folder, title, loggedUser);
		materialDAO.updateUrlName(folder, urlName, loggedUser);
		
		return Response.ok(new ApiResult<>(CompactFolderBean.fromEntity(folder))).build();
	}
	
}
