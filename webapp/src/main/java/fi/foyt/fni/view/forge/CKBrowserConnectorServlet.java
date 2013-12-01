package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.materials.FolderController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/ckbrowserconnector/", name = "forge-ckbrowser")
public class CKBrowserConnectorServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
  @Inject
  private MaterialController materialController;

	@Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
  
	@Inject
	private FolderController folderController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!sessionController.isLoggedIn()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		Long folderId = NumberUtils.createLong(request.getParameter("parent"));
		if (folderId != null) {
			Folder parentFolder = folderController.findFolderById(folderId);
			if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), parentFolder)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
		
		Action action = Action.valueOf(request.getParameter("action"));

		switch (action) {
			case LIST_MATERIALS:
  			try {
  	      handleListMaterials(request, response);
        } catch (UnsupportedEncodingException e) {
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    			return;
        } catch (JSONException e) {
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    			return;
        }
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
  private void handleListMaterials(HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException {
    Dialog dialog = Dialog.valueOf(request.getParameter("dialog").toUpperCase());
    Long folderId = NumberUtils.createLong(request.getParameter("parent"));
    User loggedUser = sessionController.getLoggedUser();
    boolean rootFolder = false;
    
    Folder parentFolder = null;
    if (folderId == null) {
    	rootFolder = true;
    } else {
    	parentFolder = folderController.findFolderById(folderId);
    }
    
    String contextPath = request.getContextPath();
    
    JSONArray materialsJson = new JSONArray();
    
    if (!rootFolder) {
    	Long grandParentId = parentFolder.getParentFolder() == null ? null : parentFolder.getParentFolder().getId();
    	String title = grandParentId == null ? ExternalLocales.getText(request.getLocale(), "forge.ckconnector.homeFolder") : parentFolder.getParentFolder().getTitle();
      materialsJson.put(createMaterialObject(grandParentId, title, contextPath + "/materials/" + parentFolder.getPath(), "ParentFolder", "up", parentFolder.getModified(), 0, parentFolder.getCreator()));
  	}
    
    List<Material> materials = null;
    
    switch (dialog) {
      case LINK:
        materials = materialController.listMaterialsByFolder(loggedUser, parentFolder);
      break;
      case IMAGE:
        materials = materialController.listMaterialsByFolderAndTypes(loggedUser, parentFolder, allowedMaterialTypes);
      break;
    }
    
    Collections.sort(materials, ComparatorUtils.chainedComparator(
      Arrays.asList(
        new MaterialTypeComparator(MaterialType.UBUNTU_ONE_ROOT_FOLDER), 
        new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER), 
        new MaterialTypeComparator(MaterialType.FOLDER)
      )
    ));

    for (Material material : materials) {
      boolean folder = material instanceof Folder;
      
      switch (dialog) {
        case LINK:
          if (folder) {
            materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Folder", getIcon(material), material.getModified(), 0, material.getCreator()));
          } else {
            materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Normal", getIcon(material), material.getModified(), 0, material.getCreator()));
          }
        break;
        case IMAGE:
          if (folder) {
            materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Normal", getIcon(material), material.getModified(), 0, material.getCreator()));
          } else {
            materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Folder", getIcon(material), material.getModified(), 0, material.getCreator()));
          }
        break;
      }
    }
    
    JSONObject result = new JSONObject();
    result.put("materials", materialsJson);
    
    response.setContentType("application/json");

    PrintWriter printWriter = response.getWriter();
    try {
    	printWriter.write(result.toString());
    } finally {
    	printWriter.flush();
    }
  }
	
	private String getIcon(Material material) {
	  if (material.getType() == MaterialType.DROPBOX_ROOT_FOLDER)
	    return "dropbox-folder";
	  
    if (material.getType() == MaterialType.UBUNTU_ONE_ROOT_FOLDER)
      return "ubuntu-one-folder";
    
    if (material instanceof Folder) {
      return "folder";
    }

	  switch (material.getType()) {
	    case DOCUMENT:
        return "document";
	    case FILE:
        return "file";
	    case IMAGE:
        return "image";
      case PDF:
        return "pdf";
      case VECTOR_IMAGE:
        return "vectorimage";
      default:
        return "file";
	  }
  }

  private JSONObject createMaterialObject(Long id, String name, String path, String type, String icon, Date date, long size, User creator) throws JSONException {
    JSONObject fileObject = new JSONObject();
    
    fileObject.put("id", id);
    fileObject.put("name", name);
    fileObject.put("path", path);
    fileObject.put("type", type);
    fileObject.put("icon", icon);
    fileObject.put("date", DATE_FORMAT.format(date));
    fileObject.put("creator", creator.getFullName());
    fileObject.put("size", getSize(size));

    return fileObject;
  }
  
  private String getSize(long size) {
    if (size > 0 && size < 1024) {
      return "1";
    } else {
      return String.valueOf(Math.round(size / 1024));
    }
  }

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
  private List<MaterialType> allowedMaterialTypes = Arrays.asList(
    MaterialType.IMAGE, 
    MaterialType.FOLDER,
    MaterialType.DROPBOX_ROOT_FOLDER,
    MaterialType.DROPBOX_FILE, 
    MaterialType.DROPBOX_FOLDER,
    MaterialType.UBUNTU_ONE_ROOT_FOLDER,
    MaterialType.UBUNTU_ONE_FOLDER,
    MaterialType.UBUNTU_ONE_FILE
  );
  
	private enum Action {
    LIST_MATERIALS
  }
	
	private enum Dialog {
		LINK,
		IMAGE
	}
}
