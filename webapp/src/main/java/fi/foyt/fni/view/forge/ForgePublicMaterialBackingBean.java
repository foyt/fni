package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.system.TagController;
import fi.foyt.fni.users.UserController;

@Join(path = "/forge/public/materials/{path}", to = "/forge/public-material.jsf")
@RequestScoped
@Named
@Stateful
public class ForgePublicMaterialBackingBean extends AbstractForgePublicViewBackingBean {

  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String path;
  
  @Inject
  private Logger logger;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private UserController userController;

  @Inject
  private TagController tagController;
  
  @Inject
  private MaterialController materialController;
  
  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @Inject
  private NavigationController navigationController;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @RequestAction
  public String init() {
    String completePath = String.format("/materials/%s", getPath());
    Material material = materialController.findMaterialByCompletePath(completePath);
    if (material == null) {
      return navigationController.notFound();
    }
    
    if (!materialPermissionController.isPublic(null, material)) {
      return navigationController.accessDenied();
    }
    
    if (material instanceof Document) {
      contentType = "text/html";
      html = ((Document) material).getData();
    } else {
      switch (material.getType()) {
        case IMAGE:
        case VECTOR_IMAGE:
          imageUrl = String.format("/materials/%s", material.getPath());
        break;
        default:
          embedUrl = String.format("/materials/%s", material.getPath());
        break;
      }
    }
    
    editLink = materialController.getForgeMaterialViewerUrl(material);
    title = material.getTitle();
    allTags = toTagBeans(materialController.listPublicMaterialTagsWithCounts(TAG_COUNT));
    
    return null;
  }
  
  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
  
  public String getTitle() {
    return title;
  }
  
  public String getContentType() {
    return contentType; 
  }
  
  public String getHtml() {
    return html;
  }

  public String getEmbedUrl() {
    return embedUrl;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public String getEditLink() {
    return editLink;
  }
  
  public List<PublicTagBean> getAllTags() {
    return allTags;
  }

  private String title;
  private String contentType;
  private String html;
  private String imageUrl;
  private String embedUrl;
  private String editLink;
  private List<PublicTagBean> allTags;
}
