package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

@Join(path = "/forge/public/users/{userId}", to = "/forge/public.jsf")
@RequestScoped
@Named
@Stateful
public class ForgePublicBackingBean {

  private static final MaterialType[] SUPPORTED_TYPES = { 
      MaterialType.DOCUMENT, 
      MaterialType.IMAGE, 
      MaterialType.PDF,
      MaterialType.FILE, 
      MaterialType.BINARY, 
      MaterialType.VECTOR_IMAGE, 
      MaterialType.GOOGLE_DOCUMENT,
      MaterialType.DROPBOX_FILE 
  };
  
  @Parameter
  private Long userId;

  @Inject
  private UserController userController;

  @Inject
  private MaterialController materialController;

  @Inject
  private NavigationController navigationController;
  
  @RequestAction
  public String init() {
    User user = userController.findUserById(getUserId());
    if (user == null) {
      return navigationController.notFound();
    }
    
    userName = user.getFullName();
    
    materials = toMaterialBeans(materialController.listPublicMaterialsByCreatorAndTypes(user, Arrays.asList(SUPPORTED_TYPES)));
    return null;
  }

  private List<Material> toMaterialBeans(List<fi.foyt.fni.persistence.model.materials.Material> materials) {
    List<Material> result = new ArrayList<>(materials.size());

    for (fi.foyt.fni.persistence.model.materials.Material material : materials) {
      String icon = materialController.getMaterialIcon(material.getType());
      
      String license = CreativeCommonsUtils.createLicenseUrl(true, true, randBoolean(), randBoolean());
      if (Math.random() > 0.9) {
        license = "https://www.mycustomlicense.org";
      }
      
      CreativeCommonsLicense commonsLicence = CreativeCommonsUtils.parseLicenseUrl(license);
      String creatorName = material.getCreator().getFullName();
      String modifierName = material.getModifier().getFullName();
      List<MaterialTag> materialTags = materialController.listMaterialTags(material);
      List<String> tags = new ArrayList<>(materialTags.size());
      
      for (MaterialTag materialTag : materialTags) {
        tags.add(materialTag.getTag().getText());
      }
      
      String viewPath = String.format("/materials/%s", material.getPath());
      String editPath = materialController.getForgeMaterialViewerUrl(material);
      
      result.add(new Material(
          material.getId(), 
          material.getTitle(), 
          icon, 
          license,
          commonsLicence != null ? commonsLicence.getIconUrl(true) : null, 
          material.getModified(), 
          creatorName, 
          modifierName,
          tags,
          viewPath,
          editPath));
    }

    return result;
  }

  private boolean randBoolean() {
    return Math.random() > 0.5;
  }

  public List<Material> getMaterials() {
    return materials;
  }
  
  public String getUserName() {
    return userName;
  }
  
  public Long getUserId() {
    return userId;
  }
  
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  private String userName;
  private List<Material> materials;

  public static class Material {

    public Material(Long id, String title, String icon, String license, String creativeCommonsIconUrl, Date modified, String creatorName, String modifierName, List<String> tags, String viewPath, String editPath) {
      super();
      this.id = id;
      this.title = title;
      this.license = license;
      this.icon = icon;
      this.creativeCommonsIconUrl = creativeCommonsIconUrl;
      this.modified = modified;
      this.creatorName = creatorName;
      this.modifierName = modifierName;
      this.tags = tags;
      this.viewPath = viewPath;
      this.editPath = editPath;
    }

    public Long getId() {
      return id;
    }
    
    public String getIcon() {
      return icon;
    }
    
    public String getTitle() {
      return title;
    }
    
    public String getLicense() {
      return license;
    }
    
    public String getCreativeCommonsIconUrl() {
      return creativeCommonsIconUrl;
    }
    
    public Date getModified() {
      return modified;
    }

    public String getCreatorName() {
      return creatorName;
    }
    
    public String getModifierName() {
      return modifierName;
    }
    
    public List<String> getTags() {
      return tags;
    }
    
    public String getEditPath() {
      return editPath;
    }
    
    public String getViewPath() {
      return viewPath;
    }
    
    private Long id;
    private String title;
    private String license;
    private String icon;
    private String creativeCommonsIconUrl;
    private Date modified;
    private String modifierName;
    private String creatorName;
    private List<String> tags;
    private String editPath;
    private String viewPath;
  }
}
