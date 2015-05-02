package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public class OrderCreatedEvent extends OrderEvent {

  public OrderCreatedEvent(Locale locale, Long orderId) {
    super(locale, orderId);
  }

}
