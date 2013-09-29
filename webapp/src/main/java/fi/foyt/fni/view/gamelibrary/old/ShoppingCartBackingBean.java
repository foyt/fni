package fi.foyt.fni.view.gamelibrary.old;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.delivery.DeliveryMehtodsController;
import fi.foyt.fni.delivery.DeliveryMethod;
import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
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

//@Stateful
//@RequestScoped
//@Named
//@URLMappings(mappings = { @URLMapping(id = "gamelibrary-cart", pattern = "/gamelibrary/cart/", viewId = "/gamelibrary/cart.jsf") })
public class ShoppingCartBackingBean implements Serializable {

	private final static double VAT_PERCENT = 24;
	private static final long serialVersionUID = -5130175554468783304L;
	
	@Inject
	private Logger logger;

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;

	@Inject
	private OrderController orderController;

	@Inject
	private SessionShoppingCartController sessionShoppingCartController;

	@Inject
	private DeliveryMehtodsController deliveryMehtodsController;

	@Inject
	private PaytrailService paytrailService;

	@PostConstruct
	public void init() {
		countrySelectItems = new ArrayList<>();

		List<Country> countries = systemSettingsController.listCountries();
		for (Country country : countries) {
			countrySelectItems.add(new SelectItem(country.getId(), country.getName()));
		}

		payerCountryId = systemSettingsController.getDefaultCountry().getId();
		deliveryMethodId = deliveryMehtodsController.getDefaultDeliveryMethod().getId();
		shoppingCartItems = new ArrayList<>();

		List<ShoppingCartItem> items = sessionShoppingCartController.getShoppingCartItems();
		for (ShoppingCartItem item : items) {
			shoppingCartItems.add(new ShoppingCartItemBean(item.getId(), item.getPublication().getName(), item.getCount(), item.getPublication().getPrice()));
		}
		
		if (sessionController.isLoggedIn()) {
			User loggedUser = sessionController.getLoggedUser();

			payerCompany = loggedUser.getCompany();
			payerFirstName = loggedUser.getFirstName();
			payerLastName = loggedUser.getLastName();
			payerEmail = userController.getUserPrimaryEmail(loggedUser);
			payerMobile = loggedUser.getMobile();
			payerTelephone = loggedUser.getPhone();

			Address address = userController.findAddressByUserAndType(loggedUser, AddressType.DELIVERY);
			if (address != null) {
				payerStreetAddress = address.getStreet1();
				payerPostalCode = address.getPostalCode();
				payerPostalOffice = address.getCity();
				payerCountryId = address.getCountry().getId();
			}
		}
	}
	
	@URLAction (phaseId = PhaseId.INVOKE_APPLICATION)
	public void applyValues() {
		List<ShoppingCartItem> cartItems = sessionShoppingCartController.getShoppingCartItems();
		Map<Long, ShoppingCartItem> itemMap = new HashMap<>();
		for (ShoppingCartItem cartItem : cartItems) {
			itemMap.put(cartItem.getId(), cartItem);
		}

		for (ShoppingCartItemBean itemBean : getShoppingCartItems()) {
			ShoppingCartItem shoppingCartItem = itemMap.get(itemBean.getId());
			if (!shoppingCartItem.getCount().equals(itemBean.getCount())) {
				sessionShoppingCartController.setItemCount(shoppingCartItem, itemBean.getCount());
			}
		}
	}

	public List<SelectItem> getCountrySelectItems() {
		return countrySelectItems;
	}

	public List<ShoppingCartItemBean> getShoppingCartItems() {
		return shoppingCartItems;
	}

	public List<DeliveryMethodBean> getDeliveryMethods() {
		ArrayList<DeliveryMethodBean> deliveryMethods = new ArrayList<>();

		List<DeliveryMethod> shoppingCartDeliveryMethods = deliveryMehtodsController.getDeliveryMethods();
		if (shoppingCartDeliveryMethods != null) {
			for (DeliveryMethod deliveryMethod : shoppingCartDeliveryMethods) {
				Country country = (this.payerCountryId != null) ? systemSettingsController.findCountryById(this.payerCountryId) : null;
				String countryCode = country != null ? country.getCode() : systemSettingsController.getDefaultCountry().getCode();
				Double weight = getItemsWeight();
				int width = getItemsWidth();
				int height = getItemsHeight();
				int depth = getItemsDepth();
				Double price = deliveryMethod.getPrice(weight, width, height, depth, countryCode);
				if (price != null) {
					String name = FacesUtils.getLocalizedValue("gamelibrary.cart." + deliveryMethod.getNameLocaleKey(weight, width, height, depth, countryCode));
					String info = FacesUtils.getLocalizedValue("gamelibrary.cart." + deliveryMethod.getInfoLocaleKey(weight, width, height, depth, countryCode));

					deliveryMethods.add(new DeliveryMethodBean(deliveryMethod.getId(), name, info, deliveryMethod.getRequiresAddress(), price));
				}
			}
		}

		return deliveryMethods;
	}

	public String getPayerFirstName() {
		return payerFirstName;
	}

	public void setPayerFirstName(String payerFirstName) {
		this.payerFirstName = payerFirstName;
	}

	public String getPayerLastName() {
		return payerLastName;
	}

	public void setPayerLastName(String payerLastName) {
		this.payerLastName = payerLastName;
	}

	public String getPayerCompany() {
		return payerCompany;
	}

	public void setPayerCompany(String payerCompany) {
		this.payerCompany = payerCompany;
	}

	public String getPayerEmail() {
		return payerEmail;
	}

	public void setPayerEmail(String payerEmail) {
		this.payerEmail = payerEmail;
	}

	public String getPayerMobile() {
		return payerMobile;
	}

	public void setPayerMobile(String payerMobile) {
		this.payerMobile = payerMobile;
	}

	public String getPayerTelephone() {
		return payerTelephone;
	}

	public void setPayerTelephone(String payerTelephone) {
		this.payerTelephone = payerTelephone;
	}

	public String getPayerStreetAddress() {
		return payerStreetAddress;
	}

	public void setPayerStreetAddress(String payerStreetAddress) {
		this.payerStreetAddress = payerStreetAddress;
	}

	public String getPayerPostalCode() {
		return payerPostalCode;
	}

	public void setPayerPostalCode(String payerPostalCode) {
		this.payerPostalCode = payerPostalCode;
	}

	public String getPayerPostalOffice() {
		return payerPostalOffice;
	}

	public void setPayerPostalOffice(String payerPostalOffice) {
		this.payerPostalOffice = payerPostalOffice;
	}

	public Long getPayerCountryId() {
		return payerCountryId;
	}

	public void setPayerCountryId(Long payerCountryId) {
		this.payerCountryId = payerCountryId;
	}

	public String getPayerCountryCode() {
		return systemSettingsController.findCountryById(getPayerCountryId()).getCode();
	}

	public String getDeliveryMethodId() {
		return deliveryMethodId;
	}

	public void setDeliveryMethodId(String deliveryMethodId) {
		this.deliveryMethodId = deliveryMethodId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
			return deliveryMethod.getPrice(getItemsWeight(), getItemsWidth(), getItemsHeight(), getItemsDepth(), getPayerCountryCode());
		}

		return 0d;
	}

	public Double getDeliveryCosts() {
		return getDeliveryCosts(getDeliveryMethodId());
	}

	public Double getNetPrice() {
		Double deliveryCosts = getDeliveryCosts();
		return (deliveryCosts != null ? deliveryCosts : 0d) + getItemCosts();
	}

	public Double getTaxAmount() {
		return getItemCosts() * (VAT_PERCENT / 100);
	}

	/**
	 * Returns total weight of items in grams
	 * 
	 * @return total weight of items in grams
	 */
	public Double getItemsWeight() {
		Double result = 0d;
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			Publication publication = item.getPublication();
			if (publication instanceof BookPublication) {
				BookPublication bookPublication = (BookPublication) publication;
				result += bookPublication.getWeight() * item.getCount();
			}
		}

		return result;
	}

	/**
	 * Returns maximum width of items in millimeters
	 * 
	 * @return maximum width of items in millimeters
	 */
	public int getItemsWidth() {
		int result = 0;
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			Publication publication = item.getPublication();
			if (publication instanceof BookPublication) {
				BookPublication bookPublication = (BookPublication) publication;
				if (bookPublication.getWidth() > result) {
					result = bookPublication.getWidth();
				}
			}
		}

		return result;
	}

	/**
	 * Returns maximum height of items in millimeters
	 * 
	 * @return maximum height of items in millimeters
	 */
	public int getItemsHeight() {
		int result = 0;
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			Publication publication = item.getPublication();
			if (publication instanceof BookPublication) {
				BookPublication bookPublication = (BookPublication) publication;
				if (bookPublication.getHeight() > result) {
					result = bookPublication.getHeight();
				}
			}
		}

		return result;
	}

	/**
	 * Returns total depth of items in millimeters
	 * 
	 * @return total depth of items in millimeters
	 */
	public int getItemsDepth() {
		int result = 0;
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			Publication publication = item.getPublication();
			if (publication instanceof BookPublication) {
				BookPublication bookPublication = (BookPublication) publication;
				result += bookPublication.getDepth() * item.getCount();
			}
		}

		return result;
	}
	
	public boolean getHasItems() {
		return getShoppingCartItems().size() > 0;
	}

	public boolean getCanProceedToCheckout() {
		for (ShoppingCartItemBean shoppingCartItem : getShoppingCartItems()) {
			if (shoppingCartItem.getCount() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public void proceedToCheckout() {
		// TODO: Delivery method
		User loggedUser = sessionController.getLoggedUser();
		String localAddress = FacesUtils.getLocalAddress(true);

		UrlSet urlSet = new UrlSet(
			localAddress + "/gamelibrary/paytrail/success", 
			localAddress + "/gamelibrary/paytrail/failure", 
			localAddress + "/gamelibrary/paytrail/notify", 
			localAddress + "/gamelibrary/paytrail/pending"
		);

		Country deliveryAddressCountry = systemSettingsController.findCountryById(getPayerCountryId());

		Address address = null;

		if (loggedUser != null) {
			address = userController.findAddressByUserAndType(loggedUser, AddressType.DELIVERY);
		}

		if (address == null) {
			address = userController.createAddress(loggedUser, AddressType.DELIVERY, getPayerStreetAddress(), null, 
			  getPayerPostalCode(), getPayerPostalOffice(), deliveryAddressCountry);
		} else {
			userController.updateAddress(address, getPayerStreetAddress(), null, getPayerPostalCode(), getPayerPostalOffice(), deliveryAddressCountry);
		}

		String streetAddess = address.getStreet1();
		if (StringUtils.isNotEmpty(address.getStreet2())) {
			streetAddess += '\n' + address.getStreet2();
		}

		String company = getPayerCompany();
		String mobile = getPayerMobile();
		String phone = getPayerTelephone();
		String firstName = getPayerFirstName();
		String lastName = getPayerLastName();
		String email = getPayerEmail();

		if (loggedUser != null) {
			userController.updateUserCompany(loggedUser, company);
			userController.updateUserMobile(loggedUser, mobile);
			userController.updateUserPhone(loggedUser, phone);
		}

		Contact contact = new Contact(firstName, lastName, email, new fi.foyt.paytrail.rest.Address(streetAddess, address.getPostalCode(), address.getCity(),
				address.getCountry().getCode()), phone, mobile, company);

		String notes = getNotes();

		Address orderAddress = userController.createAddress(address.getUser(), AddressType.DELIVERY_ARCHIVED, address.getStreet1(), address.getStreet2(),
				address.getPostalCode(), address.getCity(), address.getCountry());
		PaymentMethod paymentMethod = null;

		Order order = orderController.createOrder(loggedUser, company, email, firstName, lastName, mobile, phone, OrderStatus.NEW, paymentMethod,
				getDeliveryCosts(), notes, orderAddress);

		OrderDetails orderDetails = new OrderDetails(1, contact);
		String orderNumber = order.getId().toString();
		Payment payment = new Payment(orderNumber, orderDetails, urlSet);
		payment.setDescription(notes);

		try {
			List<ShoppingCartItem> shoppingCartItems = sessionShoppingCartController.getShoppingCartItems();
			for (ShoppingCartItem shoppingCartItem : shoppingCartItems) {
				if (shoppingCartItem.getCount() > 0) {
  				Publication publication = shoppingCartItem.getPublication();
  				OrderItem orderItem = orderController.createOrderItem(order, publication, publication.getName(), publication.getPrice(), shoppingCartItem.getCount());
  
  				paytrailService.addProduct(payment, orderItem.getName(), "#" + orderItem.getId().toString(), 
  						orderItem.getCount().doubleValue(), orderItem.getUnitPrice(), VAT_PERCENT, 0d, Product.TYPE_NORMAL);
				}
			}

			if (order.getShippingCosts() != null) {
				paytrailService.addProduct(payment, "Delivery", "", 1d, order.getShippingCosts(), VAT_PERCENT, 0d, Product.TYPE_POSTAL);
			}

			sessionShoppingCartController.deleteShoppingCart();

			Result result = paytrailService.processPayment(payment);
			if (result != null) {
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect(result.getUrl());
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Could not redirect to Paytrail", e);
					FacesUtils.addMessage(javax.faces.application.FacesMessage.SEVERITY_FATAL, e.getMessage());
				}
			} else {
				FacesUtils.addMessage(javax.faces.application.FacesMessage.SEVERITY_FATAL, "Unknown error occurred while communicating with Paytrail");
			}
		} catch (PaytrailException e) {
			logger.log(Level.SEVERE, "Error occurred while communicating with Paytrail", e);
			FacesUtils.addMessage(javax.faces.application.FacesMessage.SEVERITY_FATAL, e.getMessage());
		}
	}

	private List<SelectItem> countrySelectItems;
	private List<ShoppingCartItemBean> shoppingCartItems;

	private String payerFirstName;
	private String payerLastName;
	private String payerCompany;
	private String payerEmail;
	private String payerMobile;
	private String payerTelephone;
	private String payerStreetAddress;
	private String payerPostalCode;
	private String payerPostalOffice;
	private Long payerCountryId;
	private String deliveryMethodId;
	private String notes;

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

		public void setCount(Integer count) {
			this.count = count;
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
