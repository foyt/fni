package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/materials", to = "/illusion/event-materials.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_ACCESS)
@SecurityContext (context = "@urlName")
public class IllusionEventMaterialsBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
  
  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;
  
  @SuppressWarnings("unchecked")
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant member) {
    if (member == null) {
      return "/error/access-denied.jsf";
    }
      
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MATERIALS);
    illusionEventNavigationController.setEventUrlName(getUrlName());

    User loggedUser = sessionController.getLoggedUser();
    IllusionEventFolder folder = illusionEvent.getFolder();
    
    materials = new ArrayList<>();
    
    List<Material> allMaterials = materialController.listMaterialsByFolderAndTypes(loggedUser, folder, Arrays.asList(
      MaterialType.DOCUMENT,
      MaterialType.IMAGE,
      MaterialType.PDF,
      MaterialType.FILE,
      MaterialType.BINARY,
      MaterialType.VECTOR_IMAGE,
      MaterialType.GOOGLE_DOCUMENT,
      MaterialType.DROPBOX_FILE,
      MaterialType.CHARACTER_SHEET
    ));
    
    if (member.getRole() == IllusionEventParticipantRole.ORGANIZER) {
      materials = allMaterials;
    } else {
      for (Material material : allMaterials) {
        if (materialPermissionController.isPublic(loggedUser, material)) {
          materials.add(material);
        } else {
          if (materialPermissionController.hasAccessPermission(loggedUser, material)) {
            materials.add(material);
          }
        }
      }
    }
    
    Collections.sort(materials, ComparatorUtils.chainedComparator(
      Arrays.asList(
        new MaterialTypeComparator(MaterialType.ILLUSION_FOLDER),
        new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER),
        new MaterialTypeComparator(MaterialType.FOLDER), 
        new TitleComparator())
      )
    );
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public List<Material> getMaterials() {
    return materials;
  }
  
  public String getRelativePath(Material material) {
    List<String> path = new ArrayList<>();
    
    Material current = material;
    do {
      path.add(0, current.getUrlName());
    } while ((current == null)||(current.getType() == MaterialType.ILLUSION_FOLDER));
    
    return StringUtils.join(path, "/");
  }
  
  public boolean isViewable(Material material) {
    switch (material.getType()) {
      case FILE:
      case PDF:
        return false;
      default:
        return true;
    }
  }
  
  private List<Material> materials;
}
