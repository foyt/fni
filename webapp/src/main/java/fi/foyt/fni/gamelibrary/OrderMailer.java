package fi.foyt.fni.gamelibrary;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.mail.MailUtils;

public class OrderMailer {
	
	@Inject
	private Logger logger;
	
	@Inject
	private OrderController orderController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	public void onOrderShipped(@Observes @OrderShipped OrderEvent event) {
	  if (event.getOrderId() != null) {
      Order order = orderController.findOrderById(event.getOrderId()); 
      if (order != null) {
        Locale locale = event.getLocale();
        String customerName = order.getCustomerFirstName() + ' ' + order.getCustomerLastName();
        String customerEmail = order.getCustomerEmail();
        
        List<OrderItem> items = orderController.listOrderItems(order);
        
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.shipped.contentGreeting", customerName, order.getId()));
        contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.shipped.contentText"));
        
        if (order.getDeliveryAddress() != null) {
          String streetAddress = order.getDeliveryAddress().getStreet1();
          if (StringUtils.isNotBlank(order.getDeliveryAddress().getStreet2())) {
            streetAddress += '\n' + order.getDeliveryAddress().getStreet2();
          }
          contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.shipped.contentAddress", streetAddress, order.getDeliveryAddress().getPostalCode() + ' ' + order.getDeliveryAddress().getCity(), order.getDeliveryAddress().getCountry().getName()));
        }
        
        Double totalCosts = 0d;
        
        StringBuilder itemsList = new StringBuilder();
        for (int i = 0, l = items.size(); i < l; i++) {
          OrderItem item = items.get(i);
          itemsList.append(item.getCount());
          itemsList.append(" x ");
          itemsList.append(item.getName());
          if (i < (l - 1)) {
            itemsList.append('\n');
          }
          
          totalCosts += item.getUnitPrice() * item.getCount();
        }
        
        totalCosts += order.getShippingCosts();
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
        currencyInstance.setCurrency(Currency.getInstance("EUR"));
        String totalCostsFormatted = currencyInstance.format(totalCosts);
        
        contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.shipped.contentProducts", itemsList.toString(), totalCostsFormatted));
        contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.shipped.contentFooter"));
        
        String subject = getLocalizedValue(locale, "gamelibrary.mail.shipped.subject");
        String content = contentBuilder.toString();
        
        try {
          notifyShopOwner("Fwd: " + subject, content);
        } catch (MessagingException e) {
          logger.severe("Failed to notify shop owner of mail payment");
        }
        notifyCustomer(customerName, customerEmail, subject, content);
      } else {
        logger.severe("Tried to mail 'waiting for delivery' mail for non-existing order");
      }
    } else {
      logger.severe("Tried to mail 'waiting for delivery' mail for non-existing orderId");
    }
	}

	public void onOrderWaitingForDelivery(@Observes @OrderWaitingForDelivery OrderEvent event) {
		if (event.getOrderId() != null) {
  		Order order = orderController.findOrderById(event.getOrderId()); 
  		if (order != null) {
  		  Locale locale = event.getLocale();
  		  String customerName = order.getCustomerFirstName() + ' ' + order.getCustomerLastName();
  	    String customerEmail = order.getCustomerEmail();
  	    
  		  List<OrderItem> items = orderController.listOrderItems(order);
  		  
  	    StringBuilder contentBuilder = new StringBuilder();
  	    contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.waitingForDelivery.contentGreeting", customerName, order.getId()));
  	    contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.waitingForDelivery.contentText"));
  	            
  	    if (order.getDeliveryAddress() != null) {
  	      String streetAddress = order.getDeliveryAddress().getStreet1();
  	      if (StringUtils.isNotBlank(order.getDeliveryAddress().getStreet2())) {
  	        streetAddress += '\n' + order.getDeliveryAddress().getStreet2();
  	      }
  	      contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.waitingForDelivery.contentAddress", streetAddress, order.getDeliveryAddress().getPostalCode() + ' ' + order.getDeliveryAddress().getCity(), order.getDeliveryAddress().getCountry().getName()));
  	    }
  	    
  	    Double totalCosts = 0d;
  	    
  	    StringBuilder itemsList = new StringBuilder();
  	    for (int i = 0, l = items.size(); i < l; i++) {
  	      OrderItem item = items.get(i);
  	      itemsList.append(item.getCount());
  	      itemsList.append(" x ");
  	      itemsList.append(item.getName());
  	      if (i < (l - 1)) {
  	        itemsList.append('\n');
  	      }
  	      
  	      totalCosts += item.getUnitPrice() * item.getCount();
  	    }
  	    
  	    totalCosts += order.getShippingCosts();
  	    NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
  	    currencyInstance.setCurrency(Currency.getInstance("EUR"));
  	    String totalCostsFormatted = currencyInstance.format(totalCosts);
  	    
  	    contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.waitingForDelivery.contentProducts", itemsList.toString(), totalCostsFormatted));
  	    contentBuilder.append(getLocalizedValue(locale, "gamelibrary.mail.waitingForDelivery.contentFooter"));
  	    
  	    String subject = getLocalizedValue(locale, "gamelibrary.mail.waitingForDelivery.subject");
  		  String content = contentBuilder.toString();
  	    
  		  try {
          notifyShopOwner("Fwd: " + subject, content);
        } catch (MessagingException e) {
          logger.severe("Failed to notify shop owner of mail payment");
        }
  			notifyCustomer(customerName, customerEmail, subject, content);
  		} else {
  			logger.severe("Tried to mail 'waiting for delivery' mail for non-existing order");
  		}
		} else {
			logger.severe("Tried to mail 'waiting for delivery' mail for non-existing orderId");
		}
	}

  private void notifyCustomer(String customerName, String customerEmail, String subject, String content) {
    String fromName = systemSettingsController.getSetting(SystemSettingKey.GAMELIBRARY_ORDERMAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.GAMELIBRARY_ORDERMAILER_MAIL);
    
    try {
    	MailUtils.sendMail(fromMail, fromName, customerEmail, customerName, subject, content, "text/plain");
    } catch (MessagingException e) {
    	logger.log(Level.SEVERE, "Failed to send 'waiting for delivery' mail", e);
    }
  }
  
  private void notifyShopOwner(String subject, String content) throws MessagingException {
    String shopOwnerName = systemSettingsController.getSetting(SystemSettingKey.GAMELIBRARY_SHOP_OWNER_NAME);
    String shopOwnerEmail = systemSettingsController.getSetting(SystemSettingKey.GAMELIBRARY_SHOP_OWNER_MAIL);
    String fromName = systemSettingsController.getSetting(SystemSettingKey.GAMELIBRARY_ORDERMAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.GAMELIBRARY_ORDERMAILER_MAIL);
    
    MailUtils.sendMail(fromMail, fromName, shopOwnerEmail, shopOwnerName, subject, content, "text/plain");
  }

	private String getLocalizedValue(Locale locale, String key, Object... params) {
		return ExternalLocales.getText(locale, key, params);
	}

}
