package fi.foyt.fni.view.forge.old;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.ComparatorUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.foyt.fni.materials.MaterialArchetype;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

//@RequestScoped
//@Stateful
public class CKEditorConnectorViewController extends AbstractViewController {

	@Inject
	private SystemSettingsController systemSettingsController;
	
  @Inject
  private MaterialController materialController;

	@Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
  
	@Inject
	private FolderDAO folderDAO;
	
	@Override
	public boolean checkPermissions(ViewControllerContext context) {
	  if (!sessionController.isLoggedIn())
	    return false;

		Long folderId = context.getLongParameter("parent");
		if (folderId == null) {
			return true;
		}
		
		Folder parentFolder = folderDAO.findById(folderId);

		return materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), parentFolder);
	}

	@Override
	public void execute(ViewControllerContext context) {
		Action action = Action.valueOf(context.getStringParameter("action"));

		switch (action) {
			case LIST_MATERIALS:
  			try {
  	      handleListMaterials(context);
        } catch (UnsupportedEncodingException e) {
        	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "error.generic.configurationError"));
        } catch (JSONException e) {
        	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "error.generic.configurationError"));
        }
			break;
		}

	}
	
	@SuppressWarnings("unchecked")
  private void handleListMaterials(ViewControllerContext context) throws JSONException, UnsupportedEncodingException {
    Dialog dialog = Dialog.valueOf(context.getStringParameter("dialog").toUpperCase());
    Long folderId = context.getLongParameter("parent");
    User loggedUser = sessionController.getLoggedUser();
    boolean rootFolder = false;
    
    Folder parentFolder = null;
    if (folderId == null) {
    	rootFolder = true;
    } else {
    	parentFolder = folderDAO.findById(folderId);
    }
    
    String iconPath = systemSettingsController.getThemePath(context.getRequest()) + "/gfx/icons/16x16/";
    
    JSONArray materialsJson = new JSONArray();
    
    if (!rootFolder) {
    	Long grandParentId = parentFolder.getParentFolder() == null ? null : parentFolder.getParentFolder().getId();
    	String title = grandParentId == null ? Locales.getText(context.getRequest().getLocale(), "generic.homeFolder") : parentFolder.getParentFolder().getTitle();
      materialsJson.put(createMaterialObject(grandParentId, title, context.getRequest().getContextPath() + "/" + parentFolder.getPath(), "ParentFolder", iconPath + "actions/folder-up.png", parentFolder.getModified(), 0));
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
      switch (dialog) {
        case LINK:
          materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), context.getRequest().getContextPath() + "/" + material.getPath(), "Folder", iconPath + getIcon(material), material.getModified(), 0));
        break;
        case IMAGE:
          MaterialArchetype archetype = materialController.getMaterialArchetype(material);
          switch (archetype) {
            case IMAGE:
              materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), context.getRequest().getContextPath() + "/" + material.getPath(), "Normal", iconPath + getIcon(material), material.getModified(), 0));
            break;
            case FOLDER:
              materialsJson.put(createMaterialObject(material.getId(), material.getTitle(), context.getRequest().getContextPath() + "/" + material.getPath(), "Folder", iconPath + getIcon(material), material.getModified(), 0));
            break;
            default:
            break;
          }
        break;
      }
    }
    
    JSONObject result = new JSONObject();
    result.put("materials", materialsJson);
    result.put("status", "OK");
    
    context.setData(new TypedData(result.toString().getBytes("UTF-8"), "application/json"));
  }
	
	private String getIcon(Material material) {
	  if (material.getType() == MaterialType.DROPBOX_ROOT_FOLDER)
	    return "mimetypes/dropbox-folder.png";
	  
    if (material.getType() == MaterialType.UBUNTU_ONE_ROOT_FOLDER)
      return "mimetypes/ubuntuone-folder.png";

    MaterialArchetype materialArchetype = materialController.getMaterialArchetype(material);
	  switch (materialArchetype) {
	    case DOCUMENT:
        return "mimetypes/document.png";
	    case FILE:
        return "mimetypes/file.png";
	    case FOLDER:
        return "mimetypes/folder.png";
	    case IMAGE:
        return "mimetypes/image.png";
      case PDF:
        return "mimetypes/pdf.png";
      case VECTOR_IMAGE:
        return "mimetypes/vectorimage.png";
      default:
        return "";
	  }
  }

  private JSONObject createMaterialObject(Long id, String name, String path, String type, String iconUrl, Date date, long size) throws JSONException {
    JSONObject fileObject = new JSONObject();
    
    fileObject.put("id", id);
    fileObject.put("name", name);
    fileObject.put("path", path);
    fileObject.put("type", type);
    fileObject.put("iconUrl", iconUrl);
    fileObject.put("date", DATE_FORMAT.format(date));
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