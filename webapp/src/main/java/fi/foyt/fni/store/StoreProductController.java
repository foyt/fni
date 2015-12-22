package fi.foyt.fni.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationTagDAO;
import fi.foyt.fni.persistence.dao.store.StoreProductDAO;
import fi.foyt.fni.persistence.dao.store.StoreProductTagDAO;
import fi.foyt.fni.persistence.dao.store.StoreTagDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.store.StoreProductTag;
import fi.foyt.fni.persistence.model.store.StoreTag;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.servlet.RequestUtils;

public class StoreProductController {

  @Inject
  private StoreProductDAO storeProductDAO;
  
  @Inject
  private PublicationTagDAO publicationTagDAO;

  @Inject
  private StoreTagDAO storeTagDAO;
  
  @Inject
  private StoreProductTagDAO storeProductTagDAO;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private UserController userController;
    
  @Inject
  private ForumController forumController;
  
  public StoreProduct findStoreProductById(Long id) {
    return storeProductDAO.findById(id);
  }

  public StoreProduct findStoreProductByUrlNam(String urlName) {
    return storeProductDAO.findByUrlName(urlName);
  }

  public List<StoreProduct> listUnpublishedStoreProducts() {
    return storeProductDAO.listByPublished(Boolean.FALSE);
  }

  public List<StoreProduct> listPublishedStoreProducts() {
    return storeProductDAO.listByPublished(Boolean.TRUE);
  }

  public List<StoreProduct> listProductsByTags(String[] tags) {
    List<StoreTag> productTags = new ArrayList<>();
    
    for (String tag : tags) {
      StoreTag storeTag = storeTagDAO.findByText(tag);
      if (storeTag != null) {
        productTags.add(storeTag);
      }
    }
    
    return storeProductTagDAO.listProductsByStoreTags(productTags);
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
    
    List<StoreTag> productTags = new ArrayList<>();
    
    for (String tag : tags) {
      StoreTag storeTag = storeTagDAO.findByText(tag);
      if (storeTag == null) {
        storeTag = storeTagDAO.create(tag);
      }
      
      productTags.add(storeTag);
    }
    
    updateTags(storeProduct, productTags);
    
    return storeProduct;
  }
  
  private StoreProduct updateTags(StoreProduct storeProduct, List<StoreTag> tags) {
    List<StoreTag> addTags = new ArrayList<>(tags);
    
    Map<Long, StoreProductTag> existingTagMap = new HashMap<>();
    List<StoreProductTag> existingTags = storeProductTagDAO.listByProduct(storeProduct);
    for (StoreProductTag existingTag : existingTags) {
      existingTagMap.put(existingTag.getTag().getId(), existingTag);
    }
    
    for (int i = addTags.size() - 1; i >= 0; i--) {
      StoreTag addTag = addTags.get(i);
      
      if (existingTagMap.containsKey(addTag.getId())) {
        addTags.remove(i);
      } 
      
      existingTagMap.remove(addTag.getId());
    }
    
    for (StoreProductTag removeTag : existingTagMap.values()) {
      storeProductTagDAO.delete(removeTag);
    }
    
    for (StoreTag addTag : addTags) {
      storeProductTagDAO.create(addTag, storeProduct);
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
    if (storeProduct.getForumTopic() == null) {
      Long forumId = systemSettingsController.getLongSetting(SystemSettingKey.GAMELIBRARY_PUBLICATION_FORUM_ID);
      String systemUserEmail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_USER_EMAIL);
      User systemUser = userController.findUserByEmail(systemUserEmail);
      Forum gameLibraryForum = forumController.findForumById(forumId);
      
      if ((gameLibraryForum != null) && (systemUser != null)) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String subject = storeProduct.getName();
        String link = String.format("%s/store/%s", contextPath, storeProduct.getUrlName());

        Locale publicationLocale = storeProduct.getLanguage().getLocale();
        String initialMessage = ExternalLocales.getText(publicationLocale, "gamelibrary.forum.initialMessage", link, subject);
        
        ForumTopic topic = forumController.createTopic(gameLibraryForum, subject, systemUser);
        forumController.createForumPost(topic, systemUser, initialMessage);
        
        storeProductDAO.updateForumTopic(storeProduct, topic);
      }
    }
    
    return storeProductDAO.updatePublished(storeProduct, Boolean.TRUE);
  }

  public StoreProduct unpublishStoreProduct(StoreProduct storeProduct) {
    return storeProductDAO.updatePublished(storeProduct, Boolean.FALSE);
  }

  public void deleteStoreProduct(StoreProduct storeProduct) {
    List<PublicationTag> tags = publicationTagDAO.listByPublication(storeProduct);
    for (PublicationTag tag : tags) {
      publicationTagDAO.delete(tag);
    }
    
    storeProductDAO.delete(storeProduct);
  }
  
  /* Tags */

  public List<StoreProductTag> listProductTags(StoreProduct storeProduct) {
    return storeProductTagDAO.listByProduct(storeProduct);
  }

  public List<StoreTag> listStoreTags() {
    return storeTagDAO.listAll();
  }

}
