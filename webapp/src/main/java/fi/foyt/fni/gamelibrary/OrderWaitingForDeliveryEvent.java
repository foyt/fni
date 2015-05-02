package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public class OrderWaitingForDeliveryEvent extends OrderEvent {

  public OrderWaitingForDeliveryEvent(Locale locale, Long orderId) {
    super(locale, orderId);
  }

}
