package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public class OrderShippedEvent extends OrderEvent {

  public OrderShippedEvent(Locale locale, Long orderId) {
    super(locale, orderId);
  }

}
