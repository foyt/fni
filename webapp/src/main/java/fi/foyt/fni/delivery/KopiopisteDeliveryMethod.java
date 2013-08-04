package fi.foyt.fni.delivery;

import java.util.Currency;
import java.util.Locale;

public class KopiopisteDeliveryMethod implements DeliveryMethod {

	@Override
	public Double getPrice(int weight, int width, int height, int depth, String countryCode) {
		return 0d;
	}

	@Override
	public Currency getCurrency() {
		return Currency.getInstance("EUR");
	}

	@Override
	public String getName(Locale locale) {
		return "Nouto Kopiopisteestä";
	}

	@Override
	public String getId() {
		return "pickup-kopiopiste";
	}

	@Override
	public boolean getRequiresAddress() {
		return false;
	}

	@Override
	public String getInfo(Locale locale) {
		return "Tulostettavat tuotteet noudetaan suoraan Mikkelin Kopiopiste Ky:n tiloista osoitteesta: Maaherrankatu 30 50100 Mikkeli";
	}

}
