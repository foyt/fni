package fi.foyt.fni.delivery;

import java.util.Currency;

public class KopiopisteDeliveryMethod implements DeliveryMethod {

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
		return "deliveryMethodKopiopisteName";
	}
	
	@Override
	public String getInfoLocaleKey(Double weight, int width, int height, int depth, String countryCode) {
		return "deliveryMethodKopiopisteInfo";
	}

	@Override
	public String getId() {
		return "pickup-kopiopiste";
	}

	@Override
	public boolean getRequiresAddress() {
		return false;
	}

}
