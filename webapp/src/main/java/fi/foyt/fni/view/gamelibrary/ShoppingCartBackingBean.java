package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.delivery.DeliveryMehtodsController;
import fi.foyt.fni.delivery.DeliveryMethod;
import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.PaymentMethod;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCartItem;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.AddressType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;
import fi.foyt.paytrail.PaytrailException;
import fi.foyt.paytrail.PaytrailService;
import fi.foyt.paytrail.rest.Contact;
import fi.foyt.paytrail.rest.OrderDetails;
import fi.foyt.paytrail.rest.Payment;
import fi.foyt.paytrail.rest.Product;
import fi.foyt.paytrail.rest.Result;
import fi.foyt.paytrail.rest.UrlSet;

@Stateful
@RequestScoped
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
	private UserController userController;

	@Inject
	private SessionShoppingCartController sessionShoppingCartController;

	@Inject
	private DeliveryMehtodsController deliveryMehtodsController;

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private OrderController orderController;
	
	@Inject
	private PaytrailService paytrailService;
	
	@PostConstruct
	public void init() {
		deliveryContactFirstName = "";
		deliveryContactLastName = "";
		deliveryContactEmail = "";
		deliveryAddressStreet1 = "";
		deliveryAddressStreet2 = "";
		deliveryAddressPostalCode = "";
		deliveryAddressCity = "";
		deliveryAddressCountryId = systemSettingsController.getDefaultCountry().getId();
		deliveryContactMobile = "";
		deliveryContactTelephone = "";
		deliveryContactCompanyName = "";
		
		User loggedUser = sessionController.getLoggedUser();
		if (loggedUser != null) {
			deliveryContactFirstName = loggedUser.getFirstName();
			deliveryContactLastName = loggedUser.getLastName();
			deliveryContactEmail = userController.getUserPrimaryEmail(loggedUser);
			deliveryContactCompanyName = loggedUser.getCompany();
			deliveryContactMobile = loggedUser.getMobile();
			deliveryContactTelephone = loggedUser.getPhone();

			Address address = userController.findAddressByUserAndType(loggedUser, AddressType.DELIVERY);
			if (address != null) {
				deliveryAddressStreet1 = address.getStreet1();
				deliveryAddressStreet2 = address.getStreet2();
				deliveryAddressPostalCode = address.getPostalCode();
				deliveryAddressCity = address.getCity();
				deliveryAddressCountryId = address.getCountry().getId();
			}
		}
		
		countrySelectItems = new ArrayList<>();

		List<Country> countries = systemSettingsController.listCountries();
		for (Country country : countries) {
			countrySelectItems.add(new SelectItem(country.getId(), country.getName()));
		}

		deliveryMethodId = deliveryMehtodsController.getDefaultDeliveryMethod().getId();
	}
	
	public List<DeliveryMethodBean> getDeliveryMethods() {
		ArrayList<DeliveryMethodBean> deliveryMethods = new ArrayList<>();
		Locale locale = sessionController.getLocale();

		List<DeliveryMethod> shoppingCartDeliveryMethods = deliveryMehtodsController.getDeliveryMethods();
		if (shoppingCartDeliveryMethods != null) {
			for (DeliveryMethod deliveryMethod : shoppingCartDeliveryMethods) {
				Country country = (this.deliveryAddressCountryId != null) ? systemSettingsController.findCountryById(this.deliveryAddressCountryId) : null;
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

	public List<ShoppingCartItemBean> getShoppingCartItems() { 
		List<ShoppingCartItemBean> result = new ArrayList<>();
		
		List<ShoppingCartItem> items = sessionShoppingCartController.getShoppingCartItems();
		for (ShoppingCartItem item : items) {
			result.add(new ShoppingCartItemBean(item.getId(), item.getPublication().getName(), item.getCount(), item.getPublication().getPrice()));
		}
		
		return result;
	}
	
	public boolean isShoppingCartEmpty() {
		return sessionShoppingCartController.isShoppingCartEmpty();
	}
	
	public void incShoppingCartItemCount(Long itemId) {
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			if (item.getId().equals(itemId)) {
				sessionShoppingCartController.setItemCount(item, item.getCount() + 1);
				break;
			}
		}
	}

	public void decShoppingCartItemCount(Long itemId) {
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			if (item.getId().equals(itemId)) {
				sessionShoppingCartController.setItemCount(item, item.getCount() - 1);
				break;
			}
		}
	}

	public Double getItemCosts() {
		Double result = 0d;
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			result += item.getCount() * item.getPublication().getPrice();
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
		return systemSettingsController.findCountryById(getDeliveryAddressCountryId()).getCode();
	}
	
	public void proceedToCheckout() {
		// TODO: validate
		// TODO: What to do with payment method?
		// TODO: Notes?
		
		User loggedUser = sessionController.getLoggedUser();
		String localAddress = FacesUtils.getLocalAddress(true);
		
		UrlSet urlSet = new UrlSet(
			localAddress + "/gamelibrary/paytrail/success",	
			localAddress + "/gamelibrary/paytrail/failure",	
			localAddress + "/gamelibrary/paytrail/notify",
			localAddress + "/gamelibrary/paytrail/pending"
		);
		
		Country deliveryAddressCountry = systemSettingsController.findCountryById(getDeliveryAddressCountryId());
		
		Address address = null;
		
		if (loggedUser != null) {
		  address = userController.findAddressByUserAndType(loggedUser, AddressType.DELIVERY);
		}

	  if (address == null) {
	  	address = userController.createAddress(loggedUser, AddressType.DELIVERY, getDeliveryAddressStreet1(), 
	  			getDeliveryAddressStreet2(), getDeliveryAddressPostalCode(), getDeliveryAddressCity(), deliveryAddressCountry);
	  } else {
	  	userController.updateAddress(address, getDeliveryAddressStreet1(),  getDeliveryAddressStreet2(), 
	  			getDeliveryAddressPostalCode(), getDeliveryAddressCity(), deliveryAddressCountry);
	  }
		
		String streetAddess = address.getStreet1();
		if (StringUtils.isNotEmpty(address.getStreet2())) {
			streetAddess += '\n' + address.getStreet2();
		}
		
		String customerCompany = getDeliveryContactCompanyName();
		String customerEmail = getDeliveryContactEmail();
		String customerFirstName = getDeliveryContactFirstName();
		String customerLastName = getDeliveryContactLastName();
		String customerMobile = getDeliveryContactMobile();
		String customerPhone = getDeliveryContactTelephone();

		userController.updateUserCompany(loggedUser, customerCompany);
		userController.updateUserMobile(loggedUser, customerMobile);
		userController.updateUserPhone(loggedUser, customerPhone);

		Contact contact = new Contact(
		  customerFirstName,
			customerLastName,
			customerEmail,
			 new fi.foyt.paytrail.rest.Address(
				 streetAddess,
				 address.getPostalCode(),
				 address.getCity(),
				 address.getCountry().getCode()
			 ),
			 customerPhone,
			 customerMobile,
			 customerCompany
		);
		
		String notes = null;
		
		Address orderAddress = userController.createAddress(address.getUser(), AddressType.DELIVERY_ARCHIVED, 
				address.getStreet1(), address.getStreet2(), address.getPostalCode(), address.getCity(), address.getCountry());
		PaymentMethod paymentMethod = null;
		
		Order order = orderController.createOrder(loggedUser, 
		  customerCompany, customerEmail, customerFirstName, customerLastName, customerMobile, customerPhone,
			OrderStatus.NEW, paymentMethod, getDeliveryCosts(), notes, orderAddress);

		OrderDetails orderDetails = new OrderDetails(1, contact);
		String orderNumber = order.getId().toString();
		Payment payment = new Payment(orderNumber, orderDetails, urlSet);
		payment.setDescription(notes);
		
		try {
			List<ShoppingCartItem> shoppingCartItems = sessionShoppingCartController.getShoppingCartItems();
			for (ShoppingCartItem shoppingCartItem : shoppingCartItems) {
				Publication publication = shoppingCartItem.getPublication();
				OrderItem orderItem = orderController.createOrderItem(order, publication, publication.getName(), publication.getPrice(), shoppingCartItem.getCount());
				
				paytrailService.addProduct(payment, 
						orderItem.getName(), 
						"#" + orderItem.getId().toString(), 
						orderItem.getCount().doubleValue(),
						orderItem.getUnitPrice(),
						VAT_PERCENT,
						0d,
						Product.TYPE_NORMAL);
			}
			
			if (order.getShippingCosts() != null) {
  			paytrailService.addProduct(payment, 
  					"Delivery",
  					"",
  					1d,
  					order.getShippingCosts(),
  					VAT_PERCENT,
  					0d, 
  					Product.TYPE_POSTAL);
			}
			
			sessionShoppingCartController.deleteShoppingCart();
			
			Result result = paytrailService.processPayment(payment);
			if (result != null) {
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect(result.getUrl());
				} catch (IOException e) {
					// TODO: Handle error
				}
			} else {
				// TODO: Handle error
			}
		} catch (PaytrailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getDeliveryMethodId() {
		return deliveryMethodId;
	}
	
	public void setDeliveryMethodId(String deliveryMethodId) {
		this.deliveryMethodId = deliveryMethodId;
	}

	public String getDeliveryContactFirstName() {
		return deliveryContactFirstName;
	}

	public void setDeliveryContactFirstName(String deliveryContactFirstName) {
		this.deliveryContactFirstName = deliveryContactFirstName;
	}

	public String getDeliveryContactLastName() {
		return deliveryContactLastName;
	}

	public void setDeliveryContactLastName(String deliveryContactLastName) {
		this.deliveryContactLastName = deliveryContactLastName;
	}

	public String getDeliveryContactEmail() {
		return deliveryContactEmail;
	}

	public void setDeliveryContactEmail(String deliveryContactEmail) {
		this.deliveryContactEmail = deliveryContactEmail;
	}

	public String getDeliveryContactMobile() {
		return deliveryContactMobile;
	}

	public void setDeliveryContactMobile(String deliveryContactMobile) {
		this.deliveryContactMobile = deliveryContactMobile;
	}

	public String getDeliveryContactTelephone() {
		return deliveryContactTelephone;
	}

	public void setDeliveryContactTelephone(String deliveryContactTelephone) {
		this.deliveryContactTelephone = deliveryContactTelephone;
	}

	public String getDeliveryContactCompanyName() {
		return deliveryContactCompanyName;
	}

	public void setDeliveryContactCompanyName(String deliveryContactCompanyName) {
		this.deliveryContactCompanyName = deliveryContactCompanyName;
	}

	public String getDeliveryAddressStreet1() {
		return deliveryAddressStreet1;
	}

	public void setDeliveryAddressStreet1(String deliveryAddressStreet1) {
		this.deliveryAddressStreet1 = deliveryAddressStreet1;
	}

	public String getDeliveryAddressStreet2() {
		return deliveryAddressStreet2;
	}

	public void setDeliveryAddressStreet2(String deliveryAddressStreet2) {
		this.deliveryAddressStreet2 = deliveryAddressStreet2;
	}

	public String getDeliveryAddressPostalCode() {
		return deliveryAddressPostalCode;
	}

	public void setDeliveryAddressPostalCode(String deliveryAddressPostalCode) {
		this.deliveryAddressPostalCode = deliveryAddressPostalCode;
	}

	public String getDeliveryAddressCity() {
		return deliveryAddressCity;
	}

	public void setDeliveryAddressCity(String deliveryAddressCity) {
		this.deliveryAddressCity = deliveryAddressCity;
	}

	public Long getDeliveryAddressCountryId() {
		return deliveryAddressCountryId;
	}

	public void setDeliveryAddressCountryId(Long deliveryAddressCountryId) {
		this.deliveryAddressCountryId = deliveryAddressCountryId;
	}

	private List<SelectItem> countrySelectItems;
	private String deliveryMethodId;
	private String deliveryContactFirstName;
	private String deliveryContactLastName;
	private String deliveryContactEmail;
	private String deliveryContactMobile;
	private String deliveryContactTelephone;
	private String deliveryContactCompanyName;
	private String deliveryAddressStreet1;
	private String deliveryAddressStreet2;
	private String deliveryAddressPostalCode;
	private String deliveryAddressCity;
	private Long deliveryAddressCountryId;
	
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
