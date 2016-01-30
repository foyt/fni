package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.OrderType;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.AddressType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
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
@Join(path = "/illusion/event/{urlName}/payment", to = "/illusion/event-payment.jsf")
public class IllusionEventPaymentBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Parameter
  private String accessCode;
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionEventController illusionEventController;

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

  @Inject
  private JadeController jadeController;

  @Inject
  private NavigationController navigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if (illusionEvent.getSignUpFee() == null) {
      return navigationController.internalError();
    }
    
    if (participant == null) {
      if (StringUtils.isBlank(getAccessCode())) {
        return navigationController.requireLogin();
      }
      
      participant = illusionEventController.findParticipantByEventAndAccessCode(illusionEvent, getAccessCode());
      if (participant == null) {
        return navigationController.requireLogin();
      }
    }

    String currentPageId = IllusionEventPage.Static.INDEX.name();
    Currency currency = systemSettingsController.getDefaultCurrency();
    
    Double signUpFee = illusionEvent.getSignUpFee();
    Double vatPercent = systemSettingsController.getVatPercent();
    Double totalAmount = signUpFee;
    Double taxAmount = totalAmount - (totalAmount / (1 + (vatPercent / 100)));
    Boolean vatRegistered = systemSettingsController.isVatRegistered();
    User user = participant.getUser();
    
    String payerCompany = user.getCompany();
    String payerFirstName = user.getFirstName();
    String payerLastName = user.getLastName();
    String payerEmail = userController.getUserPrimaryEmail(user);
    String payerMobile = user.getMobile();
    String payerTelephone = user.getPhone();
    String payerStreetAddress = null;
    String payerPostalCode = null;
    String payerPostalOffice = null;
    Long payerCountryId = systemSettingsController.getDefaultCountry().getId();
    String notes = null;

    Address address = userController.findAddressByUserAndType(user, AddressType.PAYMENT_CONTACT);
    if (address != null) {
      payerStreetAddress = address.getStreet1();
      payerPostalCode = address.getPostalCode();
      payerPostalOffice = address.getCity();
      payerCountryId = address.getCountry().getId();
    }

    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, currentPageId)
      .put("payerCompany", payerCompany)
      .put("payerFirstName", payerFirstName)
      .put("payerLastName", payerLastName)
      .put("payerEmail", payerEmail)
      .put("payerMobile", payerMobile)
      .put("payerTelephone", payerTelephone)
      .put("payerStreetAddress", payerStreetAddress)
      .put("payerPostalCode", payerPostalCode)
      .put("payerPostalOffice", payerPostalOffice)
      .put("payerCountryId", payerCountryId)
      .put("notes", notes)
      .put("currency", currency)
      .put("signUpFee", signUpFee)
      .put("vatPercent", vatPercent)
      .put("totalAmount", totalAmount)
      .put("taxAmount", taxAmount)
      .put("vatRegistered", vatRegistered)
      .addBreadcrumb(illusionEvent, "/payment", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventPayment.navigationPayment"));

    try {
      Map<String, Object> templateModel = templateModelBuilder.build(sessionController.getLocale());
      headHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/payment-head", templateModel);
      contentsHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/payment-contents", templateModel);
    } catch (JadeException | IOException e) {
      logger.log(Level.SEVERE, "Could not parse jade template", e);
      return navigationController.internalError();
    }
    
    return null;
  }
  
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public String proceedToPayment() {
    User user = sessionController.getLoggedUser();
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(getUrlName());
    IllusionEventParticipant participant = null;
    
    if (user == null) {
      participant = illusionEventController.findParticipantByEventAndAccessCode(illusionEvent, getAccessCode());
      if (participant == null) {
        return navigationController.requireLogin();
      }
      
      user = participant.getUser();
    } else {
      participant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, user);
    }
    
    if ((user == null) || (participant == null)) {
      return navigationController.requireLogin();
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    PaymentDetails details;
    try {
      details = objectMapper.readValue(getOrderDetails(), PaymentDetails.class);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to unmarshal payment details", e);
      return navigationController.internalError();
    }
    
    String baseUrl = systemSettingsController.getSiteUrl(true, true);
    UrlSet urlSet = new UrlSet(
      String.format("%s/paytrail/success", baseUrl),
      String.format("%s/paytrail/failure", baseUrl),
      String.format("%s/paytrail/notify", baseUrl),
      String.format("%s/paytrail/pending", baseUrl)
    );
    
    String accessKey = UUID.randomUUID().toString();
    Address address = userController.findAddressByUserAndType(user, AddressType.PAYMENT_CONTACT);
    Country payerAddressCountry = systemSettingsController.findCountryById(details.getPayerCountryId());

    if (address == null) {
      address = userController.createAddress(user, 
        AddressType.PAYMENT_CONTACT, 
        details.getPayerStreetAddress(), 
        null,
        details.getPayerPostalCode(), 
        details.getPayerPostalOffice(), 
        payerAddressCountry);
    } else {
      userController.updateAddress(address, 
        details.getPayerStreetAddress(), 
        null,
        details.getPayerPostalCode(), 
        details.getPayerPostalOffice(), 
        payerAddressCountry);
    }
    
    Currency currency = systemSettingsController.getDefaultCurrency();
    Double signUpFee = illusionEvent.getSignUpFee();
    Double vatPercent = systemSettingsController.getVatPercent();
    Double totalAmount = signUpFee;
    
    Address orderAddress = userController.createAddress(address.getUser(), AddressType.PAYMENT_CONTACT_ARCHIVED, address.getStreet1(), address.getStreet2(),
        address.getPostalCode(), address.getCity(), address.getCountry());

    Order order = orderController.createOrder(user, 
        accessKey, 
        details.getPayerCompany(), 
        details.getPayerEmail(), 
        details.getPayerFirstName(), 
        details.getPayerLastName(), 
        details.getPayerMobile(), 
        details.getPayerTelephone(), 
        OrderStatus.NEW, 
        OrderType.ILLUSION_EVENT,
        null, 
        details.getNotes(), 
        orderAddress);
    
    Contact contact = new Contact(
        order.getCustomerFirstName(), 
        order.getCustomerLastName(), 
        order.getCustomerEmail(), 
        new fi.foyt.paytrail.rest.Address(address.getStreet1(), address.getPostalCode(), address.getCity(), address.getCountry().getCode()), 
        order.getCustomerPhone(), 
        order.getCustomerMobile(),
        order.getCustomerCompany());
    
    OrderDetails orderDetails = new OrderDetails(1, contact);
    Payment payment = new Payment(order.getId().toString(), 
      orderDetails, 
      urlSet, 
      null, 
      null, 
      currency.getCurrencyCode(), 
      getPaymentLocale().toString(), 
      null, 
      null, 
      null);
    payment.setDescription(details.getNotes());
    
    OrderItem orderItem = orderController.createOrderItem(order, null, illusionEvent, illusionEvent.getName(), totalAmount, 1);
    
    try {
      paytrailService.addProduct(payment, orderItem.getName(), "#" + orderItem.getId().toString(), 
          orderItem.getCount().doubleValue(), orderItem.getUnitPrice(), vatPercent, 0d, Product.TYPE_NORMAL);
    } catch (PaytrailException e) {
      logger.log(Level.SEVERE, "Could not add product to Paytrail payment", e);
      return navigationController.internalError();
    }
    
    Result result = null;
    try {
      result = paytrailService.processPayment(payment);
    } catch (PaytrailException e) {
      logger.log(Level.SEVERE, "Could not process Paytrail payment", e);
      return navigationController.internalError();
    }
    
    if (result != null) {
      try {
        FacesContext.getCurrentInstance().getExternalContext()
          .redirect(result.getUrl());
        return null;
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to rediect");
        return navigationController.internalError();
      }
    } else {
      logger.log(Level.SEVERE, "Unknown error occurred while communicating with Paytrail");
      return navigationController.internalError();
    }
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

  public String getHeadHtml() {
    return headHtml;
  }

  public void setHeadHtml(String headHtml) {
    this.headHtml = headHtml;
  }

  public String getContentsHtml() {
    return contentsHtml;
  }

  public void setContentsHtml(String contentsHtml) {
    this.contentsHtml = contentsHtml;
  }
  
  public String getOrderDetails() {
    return orderDetails;
  }
  
  public void setOrderDetails(String orderDetails) {
    this.orderDetails = orderDetails;
  }
  
  public String getAccessCode() {
    return accessCode;
  }
  
  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  private String headHtml;
  private String contentsHtml;
  private String orderDetails;
  
  public static class PaymentDetails {
    
    public String getPayerCompany() {
      return payerCompany;
    }
    
    public void setPayerCompany(String payerCompany) {
      this.payerCompany = payerCompany;
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

    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
    }

    private String payerCompany;
    private String payerFirstName;
    private String payerLastName;
    private String payerEmail;
    private String payerMobile;
    private String payerTelephone;
    private String payerStreetAddress;
    private String payerPostalCode;
    private String payerPostalOffice;
    private Long payerCountryId;
    private String notes;
  }

}