package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
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

  @Inject
  private Logger logger;

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

  @Inject
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private JadeController jadeController;
  
  @SuppressWarnings("unchecked")
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if (participant == null) {
      return "/error/access-denied.jsf";
    }
    
    if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER) {
      IllusionEventPageVisibility visibility = illusionEventPageController.getPageVisibility(illusionEvent, IllusionEventPage.Static.MATERIALS.name());
      if (visibility == IllusionEventPageVisibility.HIDDEN) {
        return "/error/access-denied.jsf";
      }
    }

    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MATERIALS);
    illusionEventNavigationController.setEventUrlName(getUrlName());

    User loggedUser = sessionController.getLoggedUser();
    IllusionEventFolder folder = illusionEvent.getFolder();
    
    List<Material> materials = new ArrayList<>();
    
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
    
    if (participant.getRole() == IllusionEventParticipantRole.ORGANIZER) {
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

    List<Map<String, Object>> materialsModel = new ArrayList<>();
    
    for (Material material : materials) {
      Map<String, Object> materialModel = new HashMap<>();
      materialModel.put("id", material.getId());
      materialModel.put("type", material.getType());
      materialModel.put("title", material.getTitle());
      materialModel.put("publicity", material.getPublicity());
      materialModel.put("urlName", material.getUrlName());
      materialModel.put("path", getRelativePath(material));
      materialModel.put("downloadUrl", "/materials/" + material.getPath() + "?download=true");
      materialModel.put("viewable", isViewable(material));
      materialModel.put("icon", materialController.getMaterialIcon(material.getType()));
      materialsModel.add(materialModel);
    }
    
    try {
      IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, IllusionEventPage.Static.MATERIALS)
          .addBreadcrump(illusionEvent, "/materials", ExternalLocales.getText(sessionController.getLocale(), "illusion.breadcrumps.materials"))
          .put("materials", materialsModel)
          .addLocale("illusion.materials.title");
      
      Map<String, Object> tempalteModel = templateModelBuilder.build(sessionController.getLocale());
      headHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/materials-head", tempalteModel);
      contentsHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/materials-contents", tempalteModel);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Could not parse jade template", e);
      return "/error/internal-error.jsf";
    }
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public String getHeadHtml() {
    return headHtml;
  }
  
  public String getContentsHtml() {
    return contentsHtml;
  }
  
  private String getRelativePath(Material material) {
    List<String> path = new ArrayList<>();
    
    Material current = material;
    do {
      path.add(0, current.getUrlName());
    } while ((current == null)||(current.getType() == MaterialType.ILLUSION_FOLDER));
    
    return StringUtils.join(path, "/");
  }
  
  private boolean isViewable(Material material) {
    switch (material.getType()) {
      case FILE:
      case PDF:
        return false;
      default:
        return true;
    }
  }
  
  private String headHtml;
  private String contentsHtml;
}
