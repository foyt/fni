package fi.foyt.fni.delivery;

import java.util.Currency;

public class PostiFreeDeliveryMethod implements DeliveryMethod {
	
  @Override
  public Double getPrice(Double weight, int width, int height, int depth, String countryCode) {
    return 0d;
  }

  @Override
  public Currency getCurrency() {
    return Currency.getInstance("EUR");
  }
  
  @Override
  public String getNameLocaleKey(Double weight, int width, int height, int depth, String countryCode) {
    return "deliveryMethodPostiLetterName";
  }
  
  @Override
  public String getInfoLocaleKey(Double weight, int width, int height, int depth, String countryCode) {
    return "deliveryMethodPostiLetterInfo";
  }

  @Override
  public String getId() {
    return "free-posti";
  }

  @Override
  public boolean getRequiresAddress() {
    return true;
  }

}
