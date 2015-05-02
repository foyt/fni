package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public class OrderDeliveredEvent extends OrderEvent {

  public OrderDeliveredEvent(Locale locale, Long orderId) {
    super(locale, orderId);
  }

}
