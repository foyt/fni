package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@Join(path = "/forge/folders/{ownerId}/{urlName}", to = "/forge/folder.jsf")
@RequestScoped
@Named
@Stateful
@LoggedIn
public class ForgeFolderBackingBean extends AbstractForgeMaterialViewBackingBean {

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long ownerId;

  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String urlName;

  @Inject
  private UserController userController;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;

  @Inject
  private NavigationController navigationController;
  
  @PostConstruct
  public void init() {
    folders = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  @RequestAction
  public String load() {
   if (ownerId != null && StringUtils.isNotBlank(urlName)) {
      User owner = userController.findUserById(getOwnerId());
      if (owner == null) {
        return navigationController.notFound();
      }

      Material material = materialController.findByOwnerAndPath(owner, getUrlName());
      if (material == null) {
        return navigationController.notFound();
      }

      if (!(material instanceof Folder)) {
        return navigationController.notFound();
      }

      Folder folder = (Folder) material;

      if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), folder)) {
        return navigationController.accessDenied();
      }

      setFolderId(material.getId());
      
      List<Material> materials = materialController.listMaterialsByFolder(sessionController.getLoggedUser(), folder);

      while (folder != null) {
        folders.add(0, folder);
        folder = folder.getParentFolder();
      };

      Collections.sort(materials, ComparatorUtils.chainedComparator(
        Arrays.asList(
          new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER),
          new MaterialTypeComparator(MaterialType.FOLDER), 
          new TitleComparator())
        )
      );
      
      setMaterials(materials);

      allowCharacterSheets = material.getType() == MaterialType.ILLUSION_GROUP_FOLDER;
      
      return null;
    } else {
      return navigationController.notFound();
    }
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public List<Folder> getFolders() {
    return folders;
  }
  
  public Boolean getAllowCharacterSheets() {
    return allowCharacterSheets;
  }

  private List<Folder> folders;
  private Boolean allowCharacterSheets;
}
