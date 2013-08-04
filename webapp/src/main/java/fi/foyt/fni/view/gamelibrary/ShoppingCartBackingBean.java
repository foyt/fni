package fi.foyt.fni.view.gamelibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.delivery.DeliveryMehtodsController;
import fi.foyt.fni.delivery.DeliveryMethod;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCartItem;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;

@Stateful
@SessionScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-cart", 
		pattern = "/gamelibrary/cart/", 
		viewId = "/gamelibrary/cart.jsf"
  )
})
public class ShoppingCartBackingBean implements Serializable {

	private static final long serialVersionUID = 8752219019720302773L;

	private final static double VAT_PERCENT = 23;

	@Inject
	private SessionController sessionController;

	@Inject
	private ShoppingCartController shoppingCartController;

	@Inject
	private DeliveryMehtodsController deliveryMehtodsController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	@PostConstruct
	public void init() {
		this.addressId = -1l;
		this.addressPersonName = "";
		this.addressCompanyName = "";
		this.addressStreet1 = "";
		this.addressStreet2 = "";
		this.addressPostalCode = "";
		this.addressCity = "";
		this.addressCountryId = systemSettingsController.getDefaultCountry().getId();
		
		countrySelectItems = new ArrayList<>();

		List<Country> countries = systemSettingsController.listCountries();
		for (Country country : countries) {
			countrySelectItems.add(new SelectItem(country.getId(), country.getName()));
		}
		
		addressSelectItems = new ArrayList<>();
		// TODO: Add existing addresses
		addressSelectItems.add(new SelectItem(-1l, "New Address"));

		deliveryMethodId = deliveryMehtodsController.getDefaultDeliveryMethod().getId();
	}
	
	public List<DeliveryMethodBean> getDeliveryMethods() {
		ArrayList<DeliveryMethodBean> deliveryMethods = new ArrayList<>();
		Locale locale = sessionController.getLocale();

		List<DeliveryMethod> shoppingCartDeliveryMethods = deliveryMehtodsController.getDeliveryMethods();
		if (shoppingCartDeliveryMethods != null) {
			for (DeliveryMethod deliveryMethod : shoppingCartDeliveryMethods) {
				Country country = (this.addressCountryId != null) ? systemSettingsController.findCountryById(this.addressCountryId) : null;
				String countryCode = country != null ? country.getCode() : systemSettingsController.getDefaultCountry().getCode();
				int weight = getItemsWeight();
				int width = getItemsWidth();
				int height = getItemsHeight();
				int depth = getItemsDepth();
				Double price = deliveryMethod.getPrice(weight, width, height, depth, countryCode);

				deliveryMethods.add(new DeliveryMethodBean(
					deliveryMethod.getId(), 
					deliveryMethod.getName(locale), 
					deliveryMethod.getInfo(locale), 
					deliveryMethod.getRequiresAddress(), 
					price
				));
			}
		}
		
		return deliveryMethods;
	}

	public List<SelectItem> getCountrySelectItems() {
		return countrySelectItems;
	}

	public List<SelectItem> getAddressSelectItems() {
		return addressSelectItems;
	}

	public List<ShoppingCartItemBean> getShoppingCartItems() { 
		List<ShoppingCartItemBean> result = new ArrayList<>();
		
		List<ShoppingCartItem> items = shoppingCartController.getShoppingCartItems();
		for (ShoppingCartItem item : items) {
			result.add(new ShoppingCartItemBean(item.getId(), item.getProduct().getName(), item.getCount(), item.getProduct().getPrice()));
		}
		
		return result;
	}
	
	public boolean isShoppingCartEmpty() {
		return shoppingCartController.isShoppingCartEmpty();
	}
	
	public void incShoppingCartItemCount(Long itemId) {
		for (ShoppingCartItem item : shoppingCartController.getShoppingCartItems()) {
			if (item.getId().equals(itemId)) {
				shoppingCartController.setItemCount(item, item.getCount() + 1);
				break;
			}
		}
	}

	public void decShoppingCartItemCount(Long itemId) {
		for (ShoppingCartItem item : shoppingCartController.getShoppingCartItems()) {
			if (item.getId().equals(itemId)) {
				shoppingCartController.setItemCount(item, item.getCount() - 1);
				break;
			}
		}
	}

	public Double getItemCosts() {
		Double result = 0d;
		for (ShoppingCartItem item : shoppingCartController.getShoppingCartItems()) {
			result += item.getCount() * item.getProduct().getPrice();
		}

		return result;
	}
	
	public Double getDeliveryCosts(String deliveryMethodId) {
		DeliveryMethod deliveryMethod = deliveryMehtodsController.findDeliveryMethod(deliveryMethodId);
		if (deliveryMethod != null) {
			return deliveryMethod.getPrice(getItemsWeight(), getItemsWeight(), getItemsHeight(), getItemsDepth(), getCountryCode());
		}

		return 0d;
	}
	
	public Double getDeliveryCosts() {
		return getDeliveryCosts(getDeliveryMethodId());
	}

	public Double getNetPrice() {
		return getDeliveryCosts() + getItemCosts();
	}

	public Double getTaxAmount() {
		return getItemCosts() * (VAT_PERCENT / 100);
	}

	public int getItemsWeight() {
		return 225; // a5 / 100 pages
	}
	
	public int getItemsWidth() {
		return 148; // a5;
	}
	
	public int getItemsHeight() {
		return 210; // a5;
	}
	
	public int getItemsDepth() {
		return 100; // 1mm paper * 100
	}
	
	public String getCountryCode() {
		return systemSettingsController.findCountryById(getAddressCountryId()).getCode();
	}
	
	public String proceedToCheckout() {
		return "/store/checkout.jsf";
	}
	
	public String getDeliveryMethodId() {
		return deliveryMethodId;
	}
	
	public void setDeliveryMethodId(String deliveryMethodId) {
		this.deliveryMethodId = deliveryMethodId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public String getAddressPersonName() {
		return addressPersonName;
	}

	public void setAddressPersonName(String addressPersonName) {
		this.addressPersonName = addressPersonName;
	}

	public String getAddressCompanyName() {
		return addressCompanyName;
	}

	public void setAddressCompanyName(String addressCompanyName) {
		this.addressCompanyName = addressCompanyName;
	}

	public String getAddressStreet1() {
		return addressStreet1;
	}

	public void setAddressStreet1(String addressStreet1) {
		this.addressStreet1 = addressStreet1;
	}

	public String getAddressStreet2() {
		return addressStreet2;
	}

	public void setAddressStreet2(String addressStreet2) {
		this.addressStreet2 = addressStreet2;
	}

	public String getAddressPostalCode() {
		return addressPostalCode;
	}

	public void setAddressPostalCode(String addressPostalCode) {
		this.addressPostalCode = addressPostalCode;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	public Long getAddressCountryId() {
		return addressCountryId;
	}

	public void setAddressCountryId(Long addressCountryId) {
		this.addressCountryId = addressCountryId;
	}

	private List<SelectItem> countrySelectItems;
	private List<SelectItem> addressSelectItems;
	private String deliveryMethodId;
	private Long addressId;
	private String addressPersonName;
	private String addressCompanyName;
	private String addressStreet1;
	private String addressStreet2;
	private String addressPostalCode;
	private String addressCity;
	private Long addressCountryId;
	
	public class ShoppingCartItemBean implements Serializable {
		
		private static final long serialVersionUID = -8677229900263196359L;
		
		public ShoppingCartItemBean(Long id, String name, Integer count, Double price) {
			this.id = id;
			this.name = name;
			this.count = count;
			this.price = price;
		}
		
		public Long getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public Double getPrice() {
			return price;
		}
		
		public Integer getCount() {
			return count;
		}
		
		public Double getTotalPrice() {
			return getCount() * getPrice();
		}
		
		private Long id;
		private String name;
		private Double price;
		private Integer count;
	}

	public class DeliveryMethodBean implements Serializable {

		private static final long serialVersionUID = -2501592289280027771L;

		public DeliveryMethodBean(String id, String name, String info, Boolean requiresAddress, Double price) {
			this.id = id;
			this.name = name;
			this.info = info;
			this.requiresAddress = requiresAddress;
			this.price = price;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getInfo() {
			return info;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setInfo(String info) {
			this.info = info;
		}

		public Boolean getRequiresAddress() {
			return requiresAddress;
		}

		public void setRequiresAddress(Boolean requiresAddress) {
			this.requiresAddress = requiresAddress;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		private String id;
		private String name;
		private String info;
		private Boolean requiresAddress;
		private Double price;
	}

}
