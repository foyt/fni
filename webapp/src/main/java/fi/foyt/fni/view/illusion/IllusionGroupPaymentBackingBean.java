package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
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
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.OrderType;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.AddressType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.SecurityContext;
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

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/group/{urlName}/payment", to = "/illusion/group-payment.jsf")
public class IllusionGroupPaymentBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private SessionController sessionController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private UserController userController; 

  @Inject
  private OrderController orderController;

  @Inject
  private PaytrailService paytrailService;

  @PostConstruct
  @LoggedIn
  public void postConstruct() {
    countrySelectItems = new ArrayList<>();
    
    List<Country> countries = systemSettingsController.listCountries();
    for (Country country : countries) {
      countrySelectItems.add(new SelectItem(country.getId(), country.getName()));
    }
    
  }

  @RequestAction
  @Deferred
  @LoggedIn
  public String init() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    if (illusionGroup == null) {
      return "/error/not-found.jsf";
    }

    if (illusionGroup.getSignUpFee() == null) {
      return "/error/internal-error.jsf";
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionEventParticipant groupMember = illusionGroupController.findIllusionGroupMemberByUserAndGroup(illusionGroup, loggedUser);
    if (groupMember == null) {
      return "/error/access-denied.jsf";
    }

    handlingFee = systemSettingsController.getDoubleSetting(SystemSettingKey.ILLUSION_GROUP_HANDLING_FEE);
    currency = systemSettingsController.getCurrencySetting(SystemSettingKey.ILLUSION_GROUP_HANDLING_FEE_CURRENCY);
    signUpFee = illusionGroup.getSignUpFee();
    vatPercent = systemSettingsController.getVatPercent();
    totalAmount = handlingFee + signUpFee; 
    taxAmount = totalAmount - (totalAmount / (1 + (vatPercent / 100)));
    vatRegistered = systemSettingsController.isVatRegistered();

    switch (groupMember.getRole()) {
      case BANNED:
      case BOT:
      case PENDING_APPROVAL:
        return "/error/access-denied.jsf";
      case GAMEMASTER:
      case PLAYER:
        return "/illusion/group.jsf?faces-redirect=true&urlName=" + getUrlName();
      case WAITING_PAYMENT:
      case INVITED:
        return null;
    }
    
    return "/error/internal-error.jsf";
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  @LoggedIn
  public void defaults() {
    User loggedUser = sessionController.getLoggedUser();
    Address address = userController.findAddressByUserAndType(loggedUser, AddressType.DELIVERY);
    
    countryId = systemSettingsController.getDefaultCountry().getId();
    firstName = loggedUser.getFirstName();
    lastName = loggedUser.getLastName();
    email = userController.getUserPrimaryEmail(loggedUser);
    mobile = loggedUser.getMobile();
    phone = loggedUser .getPhone();
    streetAddress = address != null ? address.getStreet1() : null;
    postalCode = address != null ? address.getPostalCode() : null;
    postalOffice = address != null ? address.getCity() : null;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public void setCountryId(Long countryId) {
    this.countryId = countryId;
  }

  public Long getCountryId() {
    return countryId;
  }
  
  public String getCompany() {
    return company;
  }
  
  public void setCompany(String company) {
    this.company = company;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getPhone() {
    return phone;
  }
  
  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getPostalOffice() {
    return postalOffice;
  }

  public void setPostalOffice(String postalOffice) {
    this.postalOffice = postalOffice;
  }
  
  public String getNotes() {
    return notes;
  }
  
  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Double getHandlingFee() {
    return handlingFee;
  }
  
  public Double getSignUpFee() {
    return signUpFee;
  }
  
  public Currency getCurrency() {
    return currency;
  }
  
  public Double getVatPercent() {
    return vatPercent;
  }
  
  public boolean isVatRegistered() {
    return vatRegistered;
  }
  
  public Double getTaxAmount() {
    return taxAmount;
  }
  
  public Double getTotalAmount() {
    return totalAmount;
  }
  
  public List<SelectItem> getCountrySelectItems() {
    return countrySelectItems;
  }
  
  public void proceedToPayment() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    String localAddress = FacesUtils.getLocalAddress(true);
    User loggedUser = sessionController.getLoggedUser();

    UrlSet urlSet = new UrlSet(
      localAddress + "/paytrail/success", 
      localAddress + "/paytrail/failure", 
      localAddress + "/paytrail/notify", 
      localAddress + "/paytrail/pending"
    );

    Address address = userController.findAddressByUserAndType(loggedUser, AddressType.DELIVERY);
    Country deliveryAddressCountry = systemSettingsController.findCountryById(getCountryId());

    if (address == null) {
      address = userController.createAddress(loggedUser, AddressType.DELIVERY, getStreetAddress(), null, getPostalCode(), getPostalOffice(), deliveryAddressCountry);
    } else {
      userController.updateAddress(address, getStreetAddress(), null, getPostalCode(), getPostalOffice(), deliveryAddressCountry);
    }

    String streetAddess = address.getStreet1();
    if (StringUtils.isNotEmpty(address.getStreet2())) {
      streetAddess += '\n' + address.getStreet2();
    }

    String company = getCompany();
    String mobile = getMobile();
    String phone = getPhone();
    String firstName = getFirstName();
    String lastName = getLastName();
    String email = getEmail();
    
    Locale paymentLocale = getPaymentLocale();
    
    userController.updateUserCompany(loggedUser, company);
    userController.updateUserMobile(loggedUser, mobile);
    userController.updateUserPhone(loggedUser, phone);

    Contact contact = new Contact(firstName, lastName, email, new fi.foyt.paytrail.rest.Address(streetAddess, address.getPostalCode(), address.getCity(),
        address.getCountry().getCode()), phone, mobile, company);

    String notes = getNotes();

    Address orderAddress = userController.createAddress(address.getUser(), AddressType.DELIVERY_ARCHIVED, address.getStreet1(), address.getStreet2(),
        address.getPostalCode(), address.getCity(), address.getCountry());

    Order order = orderController.createOrder(loggedUser, null, company, email, firstName, lastName, mobile, phone, OrderStatus.NEW, OrderType.ILLUSION_GROUP, null, notes, orderAddress);

    OrderDetails orderDetails = new OrderDetails(1, contact);
    String orderNumber = order.getId().toString();
    Payment payment = new Payment(orderNumber, orderDetails, urlSet, null, null, getCurrency().getCurrencyCode(), paymentLocale.toString(), null, null, null);
    payment.setDescription(notes);
    
    try {
      addProduct(payment, order, null, ExternalLocales.getText(paymentLocale, "illusion.group.payment.handlingFeeItem"), handlingFee, Product.TYPE_HANDLING);
      addProduct(payment, order, illusionGroup, ExternalLocales.getText(paymentLocale, "illusion.group.payment.signUpFeeItem", illusionGroup.getName()), signUpFee, Product.TYPE_NORMAL);
      
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
  
  private void addProduct(Payment payment, Order order, IllusionGroup illusionGroup, String title, Double price, Integer productType) throws PaytrailException {
    paytrailService.addProduct(payment, title, "", 1d, price, vatPercent, 0d, productType);
    orderController.createOrderItem(order, null, illusionGroup, title, price,1);
  }
  
  private Locale getPaymentLocale() {
    switch (sessionController.getLocale().getLanguage()) {
      case "fi":
        return new Locale("fi", "FI");
      case "sv":
        return new Locale("sv", "SE");
      default:      
        return new Locale("en", "US");
    }
  }

  private Long countryId;
  private String company;
  private String firstName;
  private String lastName;
  private String email;
  private String mobile;
  private String phone;
  private String streetAddress;
  private String postalCode;
  private String postalOffice;
  private String notes;
  private Double handlingFee;
  private Double signUpFee;
  private Double taxAmount;
  private Double vatPercent;
  private Double totalAmount;
  private boolean vatRegistered;
  private Currency currency;
  private List<SelectItem> countrySelectItems;
}