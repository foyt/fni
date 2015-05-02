package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public class OrderCanceledEvent extends OrderEvent {

  public OrderCanceledEvent(Locale locale, Long orderId) {
    super(locale, orderId);
  }

}
