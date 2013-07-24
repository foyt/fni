package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.StoreTag;

@SessionScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "store-product-list", 
		pattern = "/store/", 
		viewId = "/store/index.jsf"
  )
})
public class ProductListViewBackingBean {

	private static final int MAX_RECENT_PRODUCTS = 10;

	@Inject
	private StoreTagController storeTagController;

	@Inject
	private ProductController productController;

	@Inject
	private ShoppingCartController shoppingCartController;

	@Inject
	private ForumController forumController;
	
	@PostConstruct
	public void init() {
		this.categories = new ArrayList<>();

		this.categories.add(new CategoryBean(CategoryType.RECENT, "Most Recent Products", null));
		this.categories.add(new CategoryBean(CategoryType.UNPUBLISHED, "Unpublished Products", null));

		List<StoreTag> tags = storeTagController.listActiveStoreTags();
		for (StoreTag tag : tags) {
			this.categories.add(new CategoryBean(CategoryType.TAG, tag.getText(), tag.getText()));
		}
	}
	
	@URLAction (onPostback = false)
	public void load() {
		this.category = this.categories.get(0);
	}
	
	public List<Product> getProducts() {
		switch (category.getCategoryType()) {
			case RECENT:
				return productController.listRecentProducts(MAX_RECENT_PRODUCTS);
			case TAG:
				return productController.listProductsByTags(category.getTag());
			case UNPUBLISHED:
				return productController.listUnpublishedProducts();
		}

		return null;
	}
	
	public List<CategoryBean> getProductCategories(Product product) {
		List<CategoryBean> result = new ArrayList<>();
		
		List<ProductTag> productTags = storeTagController.listProductTags(product);
		for (ProductTag productTag : productTags) {
			result.add(new CategoryBean(CategoryType.TAG, productTag.getTag().getText(), productTag.getTag().getText()));
		}
		
		return result;
	}

	public List<ProductImage> getProductImages(Product product) {
		return productController.listProductImageByProduct(product);
	}

	public ProductImage getFirstImage(Product product) {
		return productController.listProductImageByProduct(product).get(0);
	}

	public boolean hasImages(Product product) {
		return productController.listProductImageByProduct(product).size() > 0;
	}

	public boolean hasSeveralImages(Product product) {
		return productController.listProductImageByProduct(product).size() > 1;
	}
	
	public boolean isBookProduct(Product product) {
		return product instanceof BookProduct;
	}
	
	public BookProduct getBookProduct(Product product) {
		return (BookProduct) product;
	}
	
	public void addProductToShoppingCart(Product product) {
		shoppingCartController.addProduct(product);
	}
	
	public Long getProductCommentCount(Product product) {
		if (product.getForumTopic() != null) {
			return forumController.countPostsByTopic(product.getForumTopic());
		}
		
		return null;
	}
	
	public List<CategoryBean> getCategories() {
		return categories;
	}

	public CategoryBean getCategory() {
		return category;
	}

	public void setCategory(CategoryBean category) {
		this.category = category;
	}

	private List<CategoryBean> categories;
	private CategoryBean category;

	public enum CategoryType {
		RECENT, 
		UNPUBLISHED, 
		TAG
	}

	public class CategoryBean {

		public CategoryBean(CategoryType categoryType, String name, String tag) {
			this.categoryType = categoryType;
			this.tag = tag;
			this.name = name;
		}

		public CategoryType getCategoryType() {
			return categoryType;
		}

		public String getTag() {
			return tag;
		}

		public String getName() {
			return name;
		}

		private CategoryType categoryType;
		private String tag;
		private String name;
	}

}