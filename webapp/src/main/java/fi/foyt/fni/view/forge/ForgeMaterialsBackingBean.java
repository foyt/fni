package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@LoggedIn
public class ForgeMaterialsBackingBean {
  
  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @PostConstruct
  public void init() {
    materialStarred = new HashMap<>();
    materialDownlodable = new HashMap<>();
    materialEditable = new HashMap<>();
    materialMovable = new HashMap<>();
    materialShareable = new HashMap<>();
    materialDeletable = new HashMap<>();
    materialPrintableAsPdf = new HashMap<>();
    materialCopyable = new HashMap<>();
  }

  public String getMaterialViewer(Material material) {
    return materialController.getForgeMaterialViewerName(material);
  }

  public String getMaterialIcon(MaterialType type) {
    return materialController.getMaterialIcon(type);
  }

  public boolean isStarred(Long materialId) {
    if (!materialStarred.containsKey(materialId)) {
      Boolean starred = false;

      Material material = materialController.findMaterialById(materialId);
      if (material != null) {
        starred = materialController.isStarred(sessionController.getLoggedUser(), material);
      }

      materialStarred.put(materialId, starred);
      return starred;
    } else {
      return materialStarred.get(materialId);
    }
  }
  
  public void starMaterial(Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material != null) {
      User loggedUser = sessionController.getLoggedUser();
      materialController.starMaterial(material, loggedUser);
      materialStarred.remove(materialId);
    }
  }

  public void unstarMaterial(Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material != null) {
      User loggedUser = sessionController.getLoggedUser();
      materialController.unstarMaterial(material, loggedUser);
      materialStarred.remove(materialId);
    }
  }

  public boolean getMaterialDownloadable(Material material) {
    if (!materialDownlodable.containsKey(material.getId())) {
      Boolean downlodable = false;
      
      if (materialController.isDownloadableType(material.getType())) {
        downlodable = materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material);
      } 
      
      materialDownlodable.put(material.getId(), downlodable);
      return downlodable;
    }  else {
      return materialDownlodable.get(material.getId());
    }
  }

  public boolean getMaterialEditable(Material material) {
    if (!materialEditable.containsKey(material.getId())) {
      Boolean editable = false;
      
      if (materialController.isEditableType(material.getType())) {
        editable = materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
      } 
      
      materialEditable.put(material.getId(), editable);
      return editable;
    }  else {
      return materialEditable.get(material.getId());
    }
  }

  public boolean getMaterialDeletable(Material material) {
    if (!materialDeletable.containsKey(material.getId())) {
      Boolean deletable = false;
      
      if (materialController.isDeletableType(material.getType())) {
        deletable = materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
      }
      
      materialDeletable.put(material.getId(), deletable);
      return deletable;
    } else {
      return materialDeletable.get(material.getId());
    }
  }

  public boolean getMaterialMovable(Material material) {
    if (!materialMovable.containsKey(material.getId())) {
      Boolean moveable = false;
      
      if (materialController.isMovableType(material.getType())) {
        moveable = materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
      }
      
      materialMovable.put(material.getId(), moveable);
      return moveable;
    } else {
      return materialMovable.get(material.getId());
    }
  }

  public boolean getMaterialShareable(Material material) {
    if (!materialShareable.containsKey(material.getId())) {
      Boolean shareable = false;
      
      if (materialController.isShareableType(material.getType())) {
        shareable = materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
      }
  
      materialShareable.put(material.getId(), shareable);
      return shareable;
    } else {
      return materialShareable.get(material.getId());
    }
  }

  public boolean getMaterialPrintableAsPdf(Material material) {
    if (!materialPrintableAsPdf.containsKey(material.getId())) {
      Boolean printableAsPdf = false;
      
      if (materialController.isPrintableAsPdfType(material.getType())) {
        printableAsPdf = materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material);
      }
      
      materialPrintableAsPdf.put(material.getId(), printableAsPdf);
      return printableAsPdf;
    } else {
      return materialPrintableAsPdf.get(material.getId());
    }
  }

  public boolean getMaterialCopyable(Material material) {
    if (!materialCopyable.containsKey(material.getId())) {
      Boolean copyable = false;
      
      if (materialController.isCopyableType(material.getType())) {
        copyable = materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
      }
  
      materialCopyable.put(material.getId(), copyable);
      return copyable;
    } else {
      return materialCopyable.get(material.getId());
    }
  }
  
  public String getMaterialCopyTargets(MaterialType materialType) {
    MaterialType[] copyTargets = materialController.getAllowedCopyTargets(materialType);
    if (copyTargets != null) {
      return StringUtils.join(copyTargets, ",");
    }
    
    return null;
  }

  public String createNewDocument(Long folderId) throws IOException {
    User loggedUser = sessionController.getLoggedUser();
    Folder parentFolder = folderId != null ? materialController.findFolderById(folderId) : null;

    if (parentFolder != null) {
      if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
        return "/error/access-denied.jsf";
      }
    }
    
    String title = FacesUtils.getLocalizedValue("forge.index.untitledDocument");
    Document document = materialController.createDocument(parentFolder, title, loggedUser);

    Long ownerId = document.getCreator().getId();
    String urlPath = document.getPath().substring(String.valueOf(ownerId).length() + 1);
    
    return String.format("/forge/documents.jsf?faces-redirect=true&ownerId=%d&urlPath=%s", ownerId, urlPath);
  }

  public String createNewVectorImage(Long folderId) throws IOException {
    User loggedUser = sessionController.getLoggedUser();
    Folder parentFolder = folderId != null ? materialController.findFolderById(folderId) : null;
    
    if (parentFolder != null) {
      if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
        return "/error/access-denied.jsf";
      }
    }
    
    String title = FacesUtils.getLocalizedValue("forge.index.untitledVectorImage");
    VectorImage vectorImage = materialController.createVectorImage(null, parentFolder, title, null, loggedUser);
    Long ownerId = vectorImage.getCreator().getId();
    String urlPath = vectorImage.getPath().substring(String.valueOf(ownerId).length() + 1);
    
    return String.format("/forge/vectorimages.jsf?faces-redirect=true&ownerId=%d&urlPath=%s", ownerId, urlPath);
  }
  
  public String createNewCharacterSheet(Long folderId) throws IOException {
    User loggedUser = sessionController.getLoggedUser();
    Folder parentFolder = folderId != null ? materialController.findFolderById(folderId) : null;
    
    if (parentFolder != null) {
      if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
        return "/error/access-denied.jsf";
      }
    }
    
    String title = FacesUtils.getLocalizedValue("forge.index.untitledCharacterSheet");
    CharacterSheet characterSheet = materialController.createCharacterSheet(parentFolder, title, null, loggedUser, null, null);

    Long ownerId = characterSheet.getCreator().getId();
    String urlPath = characterSheet.getPath().substring(String.valueOf(ownerId).length() + 1);
    
    return String.format("/forge/character-sheets.jsf?faces-redirect=true&ownerId=%d&urlPath=%s", ownerId, urlPath);
  }

  private Map<Long, Boolean> materialStarred;
  private Map<Long, Boolean> materialDownlodable;
  private Map<Long, Boolean> materialEditable;
  private Map<Long, Boolean> materialMovable;
  private Map<Long, Boolean> materialShareable;
  private Map<Long, Boolean> materialDeletable;
  private Map<Long, Boolean> materialCopyable;
  private Map<Long, Boolean> materialPrintableAsPdf;
}
