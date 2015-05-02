package fi.foyt.fni.gamelibrary;

import java.util.Locale;

public abstract class OrderEvent {
	
	public OrderEvent(Locale locale, Long orderId) {
		this.locale = locale;
		this.orderId = orderId;
	}

	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	private Locale locale;
	private Long orderId;
}
