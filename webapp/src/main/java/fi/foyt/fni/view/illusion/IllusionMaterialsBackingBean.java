package fi.foyt.fni.view.illusion;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/materials", to = "/illusion/materials.jsf")
@LoggedIn
public class IllusionMaterialsBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  public String init() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    if (illusionGroup == null) {
      return "/error/not-found.jsf";
    }
    
    User loggedUser = sessionController.getLoggedUser();
    IllusionGroupFolder folder = illusionGroup.getFolder();
    
    materials = materialController.listMaterialsByFolderAndTypes(loggedUser, folder, Arrays.asList(
      MaterialType.DOCUMENT,
      MaterialType.IMAGE,
      MaterialType.PDF,
      MaterialType.FILE,
      MaterialType.BINARY,
      MaterialType.VECTOR_IMAGE,
      MaterialType.GOOGLE_DOCUMENT,
      MaterialType.DROPBOX_FILE   
    ));
    
    id = illusionGroup.getId();
    name = illusionGroup.getName();
    description = illusionGroup.getDescription();
    illusionFolderPath = folder.getPath();
    
    return null;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<Material> getMaterials() {
    return materials;
  }
  
  public String getIllusionFolderPath() {
    return illusionFolderPath;
  }
  
  private Long id;
  private String name;
  private String description;
  private List<Material> materials;
  private String illusionFolderPath;
}
