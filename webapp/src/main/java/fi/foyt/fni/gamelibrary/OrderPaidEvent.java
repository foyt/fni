package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public class OrderPaidEvent extends OrderEvent {

  public OrderPaidEvent(Locale locale, Long orderId) {
    super(locale, orderId);
  }

}
