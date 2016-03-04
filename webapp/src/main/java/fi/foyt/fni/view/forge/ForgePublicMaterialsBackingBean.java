package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.TagController;
import fi.foyt.fni.users.UserController;

@Join(path = "/forge/public/materials/", to = "/forge/public-materials.jsf")
@RequestScoped
@Named
@Stateful
public class ForgePublicMaterialsBackingBean extends AbstractForgePublicViewBackingBean {

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
  
  @Parameter
  private String tags;

  @Inject
  private UserController userController;

  @Inject
  private TagController tagController;
  
  @Inject
  private MaterialController materialController;
  
  @RequestAction
  public String init() {
    if (getUserId() != null) {
      User user = userController.findUserById(getUserId());
      if (user != null) {
        userName = user.getFullName();
        materials = toMaterialBeans(materialController.listPublicMaterialsByCreatorAndTypes(user, Arrays.asList(SUPPORTED_TYPES)));
      }
    } else { 
      if (StringUtils.isNotBlank(getTags())) {
        List<Tag> tags = new ArrayList<>();
        for (String text : StringUtils.split(getTags(), ',')) {
          Tag tag = tagController.findTagByText(text);
          if (tag != null) {
            tags.add(tag);
          }
        }
      
        if (!tags.isEmpty()) {
          materials = toMaterialBeans(materialController.listPublicMaterialsByTags(tags));
        }
      }
    }
    
    this.allTags = toTagBeans(materialController.listPublicMaterialTagsWithCounts(TAG_COUNT));
    
    return null;
  }

  public List<PublicMaterialBean> getMaterials() {
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
  
  public String getTags() {
    return tags;
  }
  
  public void setTags(String tags) {
    this.tags = tags;
  }
  
  public List<PublicTagBean> getAllTags() {
    return allTags;
  }

  private String userName;
  private List<PublicMaterialBean> materials;
  private List<PublicTagBean> allTags;
}
