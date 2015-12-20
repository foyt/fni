package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.store.StoreProductController;

@RequestScoped
@Named
@Stateful
@Join (path = "/store/manage/{productId}/edit", to = "/store/editproduct.jsf")
@LoggedIn
public class StoreEditProductBackingBean {

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long productId;

	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private SessionController sessionController;
  
  @Inject
  private NavigationController navigationController;
  
  @Inject
  private StoreProductController storeProductController;
  
  @RequestAction
	public String load() {
    if (!sessionController.hasLoggedUserPermission(Permission.STORE_MANAGE_PRODUCTS)) {
      return navigationController.accessDenied();
    }
    
    StoreProduct storeProduct = storeProductController.findStoreProductById(getProductId());
    if (storeProduct == null) {
      return navigationController.notFound();
    }
    
    tagSelectItems = createTagSelectItems(sessionController.getLocale());
    if (tagSelectItems.size() > 0 && tagSelectItems.get(1).getSelectItems().length > 0) {
      addExistingTag = (String) tagSelectItems.get(1).getSelectItems()[0].getValue();
    }
    
    name = storeProduct.getName();
    description = storeProduct.getDescription();
    price = storeProduct.getPrice();

    tags = new ArrayList<>();
    
    List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(storeProduct);
    for (PublicationTag publicationTag : publicationTags) {
      tags.add(publicationTag.getTag().getText());
    }
		
		return null;
	}
  
  public Long getProductId() {
    return productId;
  }
  
  public void setProductId(Long productId) {
    this.productId = productId;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Double getPrice() {
    return price;
  }
  
  public void setPrice(Double price) {
    this.price = price;
  }
  
  public List<String> getTags() {
    return tags;
  }
  
  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  public String getAddExistingTag() {
    return addExistingTag;
  }
  
  public void setAddExistingTag(String addExistingTag) {
    this.addExistingTag = addExistingTag;
  }
  
  public String getAddNewTag() {
    return addNewTag;
  }
  
  public void setAddNewTag(String addNewTag) {
    this.addNewTag = addNewTag;
  }
  
  public String getRemoveTagText() {
    return removeTagText;
  }
  
  public void setRemoveTagText(String removeTagText) {
    this.removeTagText = removeTagText;
  }
  
  public List<SelectItemGroup> getTagSelectItems() {
    return tagSelectItems;
  }
  
  public void addTag() {
    String tag = getAddExistingTag();
    if ("_NEW_".equals(tag)) {
      tag = StringUtils.lowerCase(StringUtils.trim(getAddNewTag()));
    }
    
    if (StringUtils.isNotBlank(tag)) {
      if (!tags.contains(tag)) {
        tags.add(tag);
      }
    }
  }
  
  public void removeTag() {
    tags.remove(getRemoveTagText());
  }
  
  public String save() {
    StoreProduct storeProduct = storeProductController.findStoreProductById(getProductId());
    if (storeProduct == null) {
      return navigationController.notFound();
    }
    
    storeProductController.updateStoreProduct(storeProduct, getName(), getDescription(), getPrice(),
        getTags());
    
    return null;
  }
  
  private List<SelectItemGroup> createTagSelectItems(Locale locale) {
    List<GameLibraryTag> tags = gameLibraryTagController.listGameLibraryTags();
    
    ArrayList<SelectItemGroup> result = new ArrayList<>();

    List<SelectItem> tagItems = new ArrayList<>();
    for (GameLibraryTag tag : tags) {
      tagItems.add(new SelectItem(tag.getText(), StringUtils.capitalize(tag.getText())));
    }
    
    SelectItemGroup existingTagGroup = new SelectItemGroup(ExternalLocales.getText(locale, "store.editProduct.existingTagsGroup"), "", false, tagItems.toArray(new SelectItem[0]));
    SelectItemGroup newTagGroup = new SelectItemGroup(ExternalLocales.getText(locale, "store.editProduct.createTagGroup"), "", false, new SelectItem[] { new SelectItem("_NEW_", ExternalLocales.getText(locale, "store.editProduct.createTagItem")) });

    result.add(newTagGroup);
    result.add(existingTagGroup);

    return result;
  }
  
  private String name;
  private String description;
  private Double price;
  private List<String> tags;
  private String addExistingTag;
  private String addNewTag;
  private String removeTagText;

  private List<SelectItemGroup> tagSelectItems;
}
