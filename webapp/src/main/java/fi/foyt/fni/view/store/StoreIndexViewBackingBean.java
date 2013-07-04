package fi.foyt.fni.view.store;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.StoreTag;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.store.StoreController;
import fi.foyt.fni.view.AbstractViewBackingBean;

@RequestScoped
@Named
@Stateful
public class StoreIndexViewBackingBean extends AbstractViewBackingBean {

	@Inject
	private SessionController sessionController;
	
	@Inject
	private StoreController storeController;
	
	public List<StoreTagBean> getStoreTags() {
		List<StoreTagBean> result = new ArrayList<>();
		
		for (StoreTag storeTag : storeController.listTags()) {
			result.add(new StoreTagBean(storeTag.getId(), storeTag.getText()));
		}
		
		return result;
	}

	public List<ProductBean> getRecentProducts() {
		List<ProductBean> result = new ArrayList<>();
		Locale locale = sessionController.getLocale();
		
		for (Product product : storeController.listRecentProducts()) {
			List<StoreTagBean> tagBeans = new ArrayList<>();
			List<StoreTag> tags = storeController.listProductTags(product);
			for (StoreTag tag : tags) {
				tagBeans.add(new StoreTagBean(tag.getId(), tag.getText()));
			}
			
			Boolean downloadable = false;
			Boolean purchable = true;
			
			if (product instanceof BookProduct) {
				downloadable = ((BookProduct) product).getDownloadable();
			}
			
			result.add(new ProductBean(product.getId(), product.getName().getValue(locale), product.getDescription().getValue(locale), product.getPrice(), product.getDefaultImage(), downloadable, purchable, tagBeans));
		}
		
		return result;
	}
	
	public String formatProductPrice(Double price) {
		// TODO: Currency -> SystemSettings
		Currency currency = Currency.getInstance("EUR");
		NumberFormat format = NumberFormat.getCurrencyInstance(sessionController.getLocale());
		format.setCurrency(currency);
		return format.format(price);
	}
	
	public class ProductBean {

	  public ProductBean(Long id, String name, String description, Double price, ProductImage defaultImage, Boolean downloadable, Boolean purchable, List<StoreTagBean> tags) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.price = price;
			this.defaultImage = defaultImage;
			this.tags = tags;
			this.downloadable = downloadable;
			this.purchable = purchable;
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
	  
	  public Double getPrice() {
			return price;
		}
	  
	  public ProductImage getDefaultImage() {
			return defaultImage;
		}
	  
	  public List<StoreTagBean> getTags() {
			return tags;
		}
	  
	  public Boolean getDownloadable() {
			return downloadable;
		}
	  
	  public Boolean getPurchable() {
			return purchable;
		}

		private Long id;
	  
	  private String name;
	  
	  private String description;
	  
	  private Double price;
	  
	  private ProductImage defaultImage;
	  
	  private List<StoreTagBean> tags;
	  
	  private Boolean downloadable;
	  
	  private Boolean purchable;
	}

	public class StoreTagBean {
	
		public StoreTagBean(Long id, String text) {
			this.id = id;
			this.text = text;
		}
		
		public Long getId() {
			return id;
		}
		
		public String getText() {
			return text;
		}
		
		private Long id;
		
		private String text;
	}
}