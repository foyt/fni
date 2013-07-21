package fi.foyt.fni.view.forge;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.collections.ComparatorUtils;

import fi.foyt.fni.materials.MaterialArchetype;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.DropboxRootFolderDAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneRootFolderDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.view.Locales;

@RequestScoped
@Stateful
public class ForgeWorkspaceManager {
  
  @Inject
  private MaterialController materialController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private DropboxRootFolderDAO dropboxRootFolderDAO;

  @Inject
  private UbuntuOneRootFolderDAO ubuntuOneRootFolderDAO;
  
  public WorkspaceMaterialBean createRootBean(Locale locale) {
    String title = Locales.getText(locale, "generic.homeFolder");

    return new WorkspaceMaterialBean(
        null,
        null,
        null,
        title,
        MaterialType.FOLDER, 
        MaterialArchetype.FOLDER,
        "application/octet-stream",
        null,
        null,
        null,
        null,
        false,
        false,
        false,
        false,
        false,
        false
    );
  }
  
  public WorkspaceMaterialBean createBeanFromMaterial(Locale locale, User loggedUser, Material material) {
    boolean starred = materialController.isStarred(loggedUser, material);
    boolean deletableType = true;
    boolean editableType = materialController.isEditableType(material.getType());
    boolean movableType = true;
    boolean sharebleType = true;
    boolean printableAsPdf = false;
    
    switch (material.getType()) {
      case DROPBOX_FILE:
      case DROPBOX_FOLDER:
      case UBUNTU_ONE_FILE:
      case UBUNTU_ONE_FOLDER:
        deletableType = false;
        movableType = false;
        sharebleType = false;
      break;
      case DROPBOX_ROOT_FOLDER:
      case UBUNTU_ONE_ROOT_FOLDER:
        movableType = false;
        sharebleType = false;
      break;
      case DOCUMENT:
        printableAsPdf = true;
      break;
      default:
      break;
    }
    
    String title = material.getTitle();
    String normalizedTitle = Normalizer.normalize(title, Form.NFC);
    
    Boolean modifyPermission = false;
    if (editableType || deletableType) {
      modifyPermission = materialPermissionController.hasModifyPermission(loggedUser, material);
    }
    
    boolean editable = editableType && modifyPermission;
    boolean deletable = deletableType && modifyPermission;
    boolean movable = modifyPermission && movableType;
    boolean shareable = modifyPermission && sharebleType;
    
    Folder parentFolder = material.getParentFolder();
    
    return new WorkspaceMaterialBean(
        material.getId(),
        parentFolder != null ? parentFolder.getId() : null,
        parentFolder != null ? parentFolder.getTitle() : null,
        normalizedTitle,
        material.getType(),
        materialController.getMaterialArchetype(material),
        materialController.getMaterialMimeType(material),
        material.getModified(),
        material.getCreated(),
        material.getPath(),
        material.getModifier().getFullName(),
        starred,
        editable,
        movable,
        deletable,
        shareable,
        printableAsPdf
    );
  }

  @SuppressWarnings("unchecked")
  public List<WorkspaceMaterialBean> sortMaterials(List<WorkspaceMaterialBean> materialBeans) {
    Collections.sort(materialBeans, ComparatorUtils.chainedComparator(
      Arrays.asList(
        new MaterialTypeComparator(MaterialType.UBUNTU_ONE_ROOT_FOLDER), 
        new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER), 
        new MaterialTypeComparator(MaterialType.FOLDER)
      )
    ));
    
    return materialBeans;
  }
  
  public boolean getConnectedToDropbox(User user) {
    return dropboxRootFolderDAO.findByUser(user) != null;
  }
  
  public boolean getConnectedToUbuntuOne(User user) {
    return ubuntuOneRootFolderDAO.findByUser(user) != null;
  }
  
  private class MaterialTypeComparator implements Comparator<WorkspaceMaterialBean> {
    
    public MaterialTypeComparator(MaterialType type) {
      this.type = type;
    }
    
    @Override
    public int compare(WorkspaceMaterialBean o1, WorkspaceMaterialBean o2) {
      if (o1.getType() == o2.getType())
        return 0;
      
      if (o1.getType() == type)
        return -1;
      
      if (o2.getType() == type)
        return 1;

      return 0;
    }
    
    private MaterialType type;
  }
  
}
