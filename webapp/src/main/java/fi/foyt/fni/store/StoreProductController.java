package fi.foyt.fni.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationTagDAO;
import fi.foyt.fni.persistence.dao.store.StoreProductDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.servlet.RequestUtils;

public class StoreProductController {

  @Inject
  private StoreProductDAO storeProductDAO;
  
  @Inject
  private PublicationTagDAO publicationTagDAO;
  
  @Inject
  private GameLibraryTagController gameLibraryTagController;
  
  public StoreProduct findStoreProductById(Long id) {
    return storeProductDAO.findById(id);
  }
  
  public List<StoreProduct> listUnpublishedStoreProducts() {
    return storeProductDAO.listByPublished(Boolean.FALSE);
  }

  public List<StoreProduct> listPublishedStoreProducts() {
    return storeProductDAO.listByPublished(Boolean.TRUE);
  }

  public StoreProduct createStoreProduct(User creator, String name, Language language) {
    User modifier = creator;
    String urlName = createUrlName(name);
    String description = null;
    Double price = 0d;
    Double authorsShare = 0d;
    PublicationImage defaultImage = null;
    Date created = new Date();
    Integer height = null;
    Date modified = created;
    Boolean published = Boolean.FALSE;
    Integer width = null;
    Integer depth = null;
    Double weight = null;
    ForumTopic forumTopic = null;
    
    return storeProductDAO.create(name, 
        urlName, 
        description, 
        price, 
        authorsShare, 
        defaultImage, 
        created, 
        creator, 
        modified, 
        modifier, 
        published, 
        height, 
        width, 
        depth, 
        weight, 
        forumTopic, 
        language);
  }

  public StoreProduct updateStoreProduct(StoreProduct storeProduct, String name, String description, Double price,
      List<String> tags) {

    storeProductDAO.updateName(storeProduct, name);
    storeProductDAO.updateDescription(storeProduct, description);
    storeProductDAO.updatePrice(storeProduct, price);
    
    List<GameLibraryTag> productTags = new ArrayList<>();
    
    for (String tag : tags) {
      GameLibraryTag gameLibraryTag = gameLibraryTagController.findTagByText(tag);
      if (gameLibraryTag == null) {
        gameLibraryTag = gameLibraryTagController.createTag(tag);
      }
      
      productTags.add(gameLibraryTag);
    }
    
    updateTags(storeProduct, productTags);
    
    return storeProduct;
  }
  
  private StoreProduct updateTags(StoreProduct storeProduct, List<GameLibraryTag> tags) {
    List<GameLibraryTag> addTags = new ArrayList<>(tags);
    
    Map<Long, PublicationTag> existingTagMap = new HashMap<Long, PublicationTag>();
    List<PublicationTag> existingTags = gameLibraryTagController.listPublicationTags(storeProduct);
    for (PublicationTag existingTag : existingTags) {
      existingTagMap.put(existingTag.getTag().getId(), existingTag);
    }
    
    for (int i = addTags.size() - 1; i >= 0; i--) {
      GameLibraryTag addTag = addTags.get(i);
      
      if (existingTagMap.containsKey(addTag.getId())) {
        addTags.remove(i);
      } 
      
      existingTagMap.remove(addTag.getId());
    }
    
    for (PublicationTag removeTag : existingTagMap.values()) {
      gameLibraryTagController.deletePublicationTag(removeTag);
    }
    
    for (GameLibraryTag gameLibraryTag : addTags) {
      publicationTagDAO.create(gameLibraryTag, storeProduct);
    }
    
    return storeProduct;
  }
  
  private String createUrlName(String name) {
    return createUrlName(null, name);
  }
  
  private String createUrlName(StoreProduct storeProduct, String name) {
    int maxLength = 30;
    int padding = 0;
    
    do {
      String urlName = RequestUtils.createUrlName(name, maxLength);
      if (padding > 0) {
        urlName = urlName.concat(StringUtils.repeat('_', padding));
      }
      
      StoreProduct existingProduct = storeProductDAO.findByUrlName(urlName);
      if (existingProduct == null) {
        return urlName;
      }
      
      if (storeProduct != null && existingProduct.getId().equals(existingProduct.getId())) {
        return urlName;
      }
      
      if (maxLength < name.length()) {
        maxLength++;
      } else {
        padding++;
      }
    } while (true);
  }

  public StoreProduct publishStoreProduct(StoreProduct storeProduct) {
    return storeProductDAO.updatePublished(storeProduct, Boolean.TRUE);
  }

  public StoreProduct unpublishStoreProduct(StoreProduct storeProduct) {
    return storeProductDAO.updatePublished(storeProduct, Boolean.FALSE);
  }
}
