package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.store.StoreProductTag;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.store.StoreProductController;

@RequestScoped
@Named
@Stateful
@Join(path = "/store/{urlName}", to = "/store/product.jsf")
public class StoreProductBackingBean {
  
  @Parameter
  private String urlName;

  @Inject
  private StoreProductController storeProductController;
 
  @Inject
  private SessionShoppingCartController sessionShoppingCartController;

  @Inject
  private ForumController forumController;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private NavigationController navigationController; 

  @RequestAction
  public String load() {
    StoreProduct storeProduct = storeProductController.findStoreProductByUrlName(getUrlName());
    if (storeProduct == null) {
      return navigationController.notFound();
    }
        
    if (!storeProduct.getPublished()) {
      if (!sessionController.isLoggedIn()) {
        return navigationController.accessDenied();
      }

      if (!storeProduct.getCreator().getId().equals(sessionController.getLoggedUserId())) {
        if (!sessionController.hasLoggedUserRole(Role.STORE_MANAGER)) {
          return navigationController.accessDenied();
        }
      }
    }
    
    PublicationImage defaultImage = storeProduct.getDefaultImage();
    List<StoreProductTag> productTags = storeProductController.listProductTags(storeProduct);
    
    id = storeProduct.getId();
    price = storeProduct.getPrice();
    defaultImageId = defaultImage != null ? defaultImage.getId() : null;
    urlName = storeProduct.getUrlName();
    name = storeProduct.getName();
    description = storeProduct.getDescription();
    description = StringUtils.isBlank(description) ? "" : description.replace("\n", "<br/>");
    tags = new ArrayList<>(productTags.size());
    
    forumUrlName = storeProduct.getForumTopic() != null ? storeProduct.getForumTopic().getForum().getUrlName() : "";
    forumTopicUrlName = storeProduct.getForumTopic() != null ? storeProduct.getForumTopic().getUrlName() : "";
    commentCount = forumController.countPostsByTopic(storeProduct.getForumTopic());

    for (StoreProductTag productTag : productTags) {
      tags.add(productTag.getTag().getText());
    }
    
    return null;
  }

  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public String addToShoppingCart() {
    StoreProduct storeProduct = storeProductController.findStoreProductById(id);
    if (storeProduct == null) {
      return navigationController.notFound();
    }
    
    sessionShoppingCartController.addPublication(storeProduct);
    
    return "/store/index.jsf?faces-redirect=true";
  }
  
  public Long getId() {
    return id;
  }
  
  public Double getPrice() {
    return price;
  }
  
  public Long getDefaultImageId() {
    return defaultImageId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
  
  public Long getCommentCount() {
    return commentCount;
  }
  
  public String getForumTopicUrlName() {
    return forumTopicUrlName;
  }
  
  public String getForumUrlName() {
    return forumUrlName;
  }

  public List<String> getTags() {
    return tags;
  }

  private Long id;
  private Double price;
  private Long defaultImageId;
  private String name;
  private String description;
  private String forumUrlName;
  private String forumTopicUrlName;
  private Long commentCount;
  private List<String> tags;
  
}