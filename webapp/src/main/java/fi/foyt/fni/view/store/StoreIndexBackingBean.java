package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.store.StoreProductTag;
import fi.foyt.fni.store.StoreProductController;

@RequestScoped
@Named
@Stateful
@Join(path = "/store/", to = "/store/index.jsf")
public class StoreIndexBackingBean {

  @Inject
  private StoreProductController storeProductController;
 
  @Inject
  private SessionShoppingCartController sessionShoppingCartController;
  
  @Inject
  private NavigationController navigationController; 

  @RequestAction
  public String load() {
    List<StoreProduct> storeProducts = storeProductController.listPublishedStoreProducts();

    products = new ArrayList<>(storeProducts.size());
    
    for (StoreProduct storeProduct : storeProducts) {
      PublicationImage defaultImage = storeProduct.getDefaultImage();
      List<StoreProductTag> productTags = storeProductController.listProductTags(storeProduct);
      
      String urlName = storeProduct.getUrlName();
      String name = storeProduct.getName();
      String description = storeProduct.getDescription();
      description = StringUtils.isBlank(description) ? "" : description.replace("\n", "<br/>");
      List<String> tags = new ArrayList<>(productTags.size());
      
      for (StoreProductTag productTag : productTags) {
        tags.add(productTag.getTag().getText());
      }
      
      products.add(new Product(
          storeProduct.getId(),
          storeProduct.getPrice(),
          defaultImage != null ? defaultImage.getId() : null, 
          urlName, 
          name, 
          description, 
          tags));
    }

    return null;
  }

  public List<Product> getProducts() {
    return products;
  }
  
  public String addToShoppingCart(Long id) {
    StoreProduct storeProduct = storeProductController.findStoreProductById(id);
    if (storeProduct == null) {
      return navigationController.notFound();
    }
    
    sessionShoppingCartController.addPublication(storeProduct);
    
    return "/store/index.jsf?faces-redirect=true";
  }

  private List<Product> products;

  public static class Product {

    public Product(Long id, Double price, Long defaultImageId, String urlName, String name, String description, List<String> tags) {
      super();
      this.id = id;
      this.price = price;
      this.defaultImageId = defaultImageId;
      this.urlName = urlName;
      this.name = name;
      this.description = description;
      this.tags = tags;
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

    public String getUrlName() {
      return urlName;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public List<String> getTags() {
      return tags;
    }

    private Long id;
    private Double price;
    private Long defaultImageId;
    private String urlName;
    private String name;
    private String description;
    private List<String> tags;
  }
}