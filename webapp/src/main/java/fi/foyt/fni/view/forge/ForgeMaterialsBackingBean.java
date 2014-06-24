package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.materials.FolderController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.VectorImageController;
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

  @Inject
  private FolderController folderController;

  @Inject
  private DocumentController documentController;

  @Inject
  private VectorImageController vectorImageController;

  @PostConstruct
  public void init() {
    materialStarred = new HashMap<>();
    materialDownlodable = new HashMap<>();
    materialEditable = new HashMap<>();
    materialMovable = new HashMap<>();
    materialShareable = new HashMap<>();
    materialDeletable = new HashMap<>();
    materialPrintableAsPdf = new HashMap<>();
  }

  public String getMaterialViewer(Material material) {
    return materialController.getForgeMaterialViewerName(material);
  }

  public String getMaterialIcon(MaterialType type) {
    switch (type) {
      case DROPBOX_FILE:
        return "file";
      case DROPBOX_FOLDER:
        return "folder";
      case DROPBOX_ROOT_FOLDER:
        return "dropbox";
      case GOOGLE_DOCUMENT:
        return "google-drive";
      case DOCUMENT:
        return "document";
      case BINARY:
        return "file";
      case FILE:
        return "file";
      case PDF:
        return "pdf";
      case FOLDER:
        return "folder";
      case IMAGE:
        return "image";
      case VECTOR_IMAGE:
        return "vector-image";
    }

    return null;
  }

  public synchronized boolean isStarred(Long materialId) {
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
  
  public synchronized void starMaterial(Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material != null) {
      User loggedUser = sessionController.getLoggedUser();
      materialController.starMaterial(material, loggedUser);
      materialStarred.remove(materialId);
    }
  }

  public synchronized void unstarMaterial(Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material != null) {
      User loggedUser = sessionController.getLoggedUser();
      materialController.unstarMaterial(material, loggedUser);
      materialStarred.remove(materialId);
    }
  }

  public synchronized boolean getMaterialDownloadable(Material material) {
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

  public synchronized boolean getMaterialEditable(Material material) {
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

  public void createNewDocument(Long folderId) throws IOException {
    User loggedUser = sessionController.getLoggedUser();
    Folder parentFolder = folderId != null ? folderController.findFolderById(folderId) : null;
    String title = FacesUtils.getLocalizedValue("forge.index.untitledDocument");
    Document document = documentController.createDocument(parentFolder, title, loggedUser);

    FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
      .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
      .append("/forge/documents/" + document.getPath()).toString());
  }

  public void createNewVectorImage(Long folderId) throws IOException {
    User loggedUser = sessionController.getLoggedUser();
    Folder parentFolder = folderId != null ? folderController.findFolderById(folderId) : null;
    String title = FacesUtils.getLocalizedValue("forge.index.untitledVectorImage");
    VectorImage vectorImage = vectorImageController.createVectorImage(null, parentFolder, title, null, loggedUser);

    FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
      .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
      .append("/forge/vectorimages/" + vectorImage.getPath()).toString());
  }

  private Map<Long, Boolean> materialStarred;
  private Map<Long, Boolean> materialDownlodable;
  private Map<Long, Boolean> materialEditable;
  private Map<Long, Boolean> materialMovable;
  private Map<Long, Boolean> materialShareable;
  private Map<Long, Boolean> materialDeletable;
  private Map<Long, Boolean> materialPrintableAsPdf;
}
