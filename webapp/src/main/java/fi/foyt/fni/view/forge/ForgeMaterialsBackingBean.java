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
  
  public void starMaterial(Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material != null) {
      User loggedUser = sessionController.getLoggedUser();
      materialController.starMaterial(material, loggedUser);
    }
  }

  public void unstarMaterial(Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material != null) {
      User loggedUser = sessionController.getLoggedUser();
      materialController.unstarMaterial(material, loggedUser);
    }
  }

  public boolean getMaterialEditable(Material material) {
    if (materialController.isEditableType(material.getType())) {
      return materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
    }

    return false;
  }

  public boolean getMaterialDeletable(Material material) {
    if (materialController.isDeletableType(material.getType())) {
      return materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
    }

    return false;
  }

  public boolean getMaterialMovable(Material material) {
    if (materialController.isMovableType(material.getType())) {
      return materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
    }

    return false;
  }

  public boolean getMaterialShareable(Material material) {
    if (materialController.isShareableType(material.getType())) {
      return materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material);
    }

    return false;
  }

  public boolean getMaterialPrintableAsPdf(Material material) {
    if (materialController.isPrintableAsPdfType(material.getType())) {
      return materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material);
    }

    return false;
  }

  @LoggedIn
  public void createNewDocument(Long folderId) throws IOException {
    User loggedUser = sessionController.getLoggedUser();
    Folder parentFolder = folderId != null ? folderController.findFolderById(folderId) : null;
    String title = FacesUtils.getLocalizedValue("forge.index.untitledDocument");
    Document document = documentController.createDocument(parentFolder, title, loggedUser);

    FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
      .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
      .append("/forge/documents/" + document.getPath()).toString());
  }

  @LoggedIn
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
}
