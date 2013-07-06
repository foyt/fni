package fi.foyt.fni.persistence.model.store;

public enum OrderStatus {
  NEW,
  CHECKED_OUT,
  CANCELED,
  WAITING_FOR_DELIVERY,
  DELIVERED
}
