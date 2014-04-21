package fi.foyt.fni.view.forge;

import java.util.List;

import javax.inject.Inject;

import fi.foyt.fni.dropbox.DropboxController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.session.SessionController;

public abstract class AbstractForgeMaterialViewBackingBean {

  private static final int MAX_LAST_VIEWED_MATERIALS = 5;
  private static final int MAX_LAST_EDITED_MATERIALS = 5;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;

  @Inject
  private DropboxController dropboxController;
  
  protected void setMaterials(List<Material> materials) {
    this.materials = materials;
  }

  public List<Material> getMaterials() {
    return materials;
  }

  public List<Material> getLastViewedMaterials() {
    return materialController.listViewedMaterialsByUser(sessionController.getLoggedUser(), 0, MAX_LAST_VIEWED_MATERIALS);
  }

  public List<Material> getLastEditedMaterials() {
    return materialController.listModifiedMaterialsByUser(sessionController.getLoggedUser(), 0, MAX_LAST_EDITED_MATERIALS);
  }

  public List<Material> getStarredMaterials() {
    return materialController.listStarredMaterialsByUser(sessionController.getLoggedUser());
  }

  public Long getFolderId() {
    return folderId;
  }

  public void setFolderId(Long folderId) {
    this.folderId = folderId;
  }

  public boolean getDropboxLinked() {
    return dropboxController.findDropboxRootFolderByUser(sessionController.getLoggedUser()) != null;
  }

  private Long folderId;
  private List<Material> materials;
}
