package fi.foyt.fni.view.store;

import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;

public class PostiDeliveryMethod implements DeliveryMethod {
	
	private static final String[] COUNTRIES_FINLAND = {"FI"};
	private static final String[] COUNTRIES_EU = {"AT", "BE", "BG", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES", "SE", "GB"};
	private static final String[] COUNTRIES_ALL = null;
	
	private static final DeliveryMethod[] DELIVERY_METHODS = {
		
		/* Letter / Finland */
		
		new DeliveryMethod(250, 400, 30, 2000, PackType.LETTER, COUNTRIES_FINLAND, new PricingModel(new PricingModelItem[] {
	  	new PricingModelItem(  50, 0.75),
	  	new PricingModelItem( 100, 0.95),
	  	new PricingModelItem( 250, 1.50),
	  	new PricingModelItem( 500, 3.00),
	  	new PricingModelItem(1000, 4.50),
	  	new PricingModelItem(2000, 7.50)
		})),
		
		/* Maxi Letter / Finland */
		
		new DeliveryMethod(909, 900, 600, 2000, PackType.MAXI_LETTER, COUNTRIES_FINLAND, new PricingModel(new PricingModelItem[] {
	  	new PricingModelItem( 250,  3.50),
	  	new PricingModelItem( 500,  5.30),
	  	new PricingModelItem(1000,  8.00),
	  	new PricingModelItem(2000, 13.00)
		})),
		
		/* Parcel / Finland */
		
		new DeliveryMethod(1000, 600, 600, 3000, PackType.PARCEL, COUNTRIES_FINLAND, new PricingModel(new PricingModelItem[] {
			new PricingModelItem( 2000,  7.40),
			new PricingModelItem( 5000,  8.80),
			new PricingModelItem(10000, 10.00),
			new PricingModelItem(15000, 13.30),
			new PricingModelItem(30000, 18.50),
		})),
		
		/* Letter / EU */
		
		new DeliveryMethod(250, 400, 30, 2000, PackType.LETTER, COUNTRIES_EU, new PricingModel(new PricingModelItem[] {
		  new PricingModelItem(  20,  0.80),
		  new PricingModelItem(  50,  1.05),
		  new PricingModelItem( 100,  1.60),
		  new PricingModelItem( 250,  2.50),
		  new PricingModelItem( 500,  3.90),
		  new PricingModelItem(1000,  7.20),
		  new PricingModelItem(2000, 14.00)
		})),
		
		/* Maxi Letter / EU */
		
		new DeliveryMethod(909, 900, 600, 2000, PackType.MAXI_LETTER, COUNTRIES_EU, new PricingModel(new PricingModelItem[] {
			new PricingModelItem( 250,  4.80),
			new PricingModelItem( 500,  8.00),
			new PricingModelItem(1000, 12.60),
			new PricingModelItem(1500, 18.00),
			new PricingModelItem(2000, 23.50)
		})),
		
		/* Letter / Global */
		
		new DeliveryMethod(250, 400, 30, 2000, PackType.LETTER, COUNTRIES_ALL, new PricingModel(new PricingModelItem[] {
		  new PricingModelItem(  20,  0.80),
		  new PricingModelItem(  50,  1.30),
		  new PricingModelItem( 100,  2.00),
		  new PricingModelItem( 250,  3.50),
		  new PricingModelItem( 500,  6.10),
		  new PricingModelItem(1000,  9.00),
		  new PricingModelItem(2000, 18.00)
		})),
		
		/* Maxi Letter / Global */
		
		new DeliveryMethod(909, 900, 600, 2000, PackType.MAXI_LETTER, COUNTRIES_ALL, new PricingModel(new PricingModelItem[] {
			new PricingModelItem( 250,  7.50),
			new PricingModelItem( 500, 11.20),
			new PricingModelItem(1000, 18.00),
			new PricingModelItem(1500, 24.30),
			new PricingModelItem(2000, 32.00)
		}))
		
	};
  
	@Override
	public Currency getCurrency() {
		return Currency.getInstance("EUR");
	}

	@Override
	public Double getPrice(int weight, int width, int height, int depth, String countryCode) {
		int[] widthHeightDepth = new int[] { width, height, depth };
		Arrays.sort(widthHeightDepth);
		
		for (DeliveryMethod deliveryMethod : DELIVERY_METHODS) {
			if (deliveryMethod.canDeliver(widthHeightDepth, weight, countryCode)) {
				return deliveryMethod.getPrice(weight);
			}
		}
		
		return null;
	}
	
	private static class DeliveryMethod {
		
		public DeliveryMethod(int maxWidth, int maxHeight, int maxDepth, int maxWeight, PackType packType, String[] deliveryCountries, PricingModel pricingModel) {
		  this.maxDimensions = new int[] { maxWidth, maxHeight, maxDepth };
		  Arrays.sort(maxDimensions);
		  this.maxWeight = maxWeight;
		  this.packType = packType;
		  this.deliveryCountries = deliveryCountries;
		  this.pricingModel = pricingModel;
		}
		
		public boolean canDeliver(int[] dimensions, int weight, String countryCode) {
			if ((deliveryCountries != null) && (!ArrayUtils.contains(deliveryCountries, countryCode))) {
				return false;
			}
			
			if (weight > maxWeight)
				return false;
			
			for (int i = 0, l = dimensions.length; i < l; i++) {
				if (dimensions[i] > this.maxDimensions[i]) {
					return false;
				}
			}

			return true;
		}
		
		@SuppressWarnings("unused")
		public PackType getPackType() {
			return packType;
		}
		
		public Double getPrice(int weight) {
			return pricingModel.getItemByWeight(weight).getPrice();
		}

		private int[] maxDimensions;
		private int maxWeight;
		private PackType packType;
		private String[] deliveryCountries;
		private PricingModel pricingModel;
	}
	
	private enum PackType {
		LETTER,
		MAXI_LETTER,
		PARCEL
	}
	
	private static class PricingModel {
		
		public PricingModel(PricingModelItem[] items) {
			this.items = items;
		}
		
		public PricingModelItem getItemByWeight(int weight) {
			for (PricingModelItem item : getItems()) {
				if (weight <= item.getMaxWeight()) {
					return item;
				}
			}
			
			return null;
		}

		public PricingModelItem[] getItems() {
			return items;
		}
		
		private PricingModelItem[] items;
	}
	
	private static class PricingModelItem {
		
		public PricingModelItem(int maxWeight, Double price) {
			this.maxWeight = maxWeight;
			this.price = price;
		}
		
		public int getMaxWeight() {
			return maxWeight;
		}
		
		public Double getPrice() {
			return price;
		}
		
		private int maxWeight; 
		private Double price;
	}

	@Override
	public String getName(Locale locale) {
		// TODO Auto-generated method stub
		return "Posti";
	}

	@Override
	public String getId() {
		return "posti";
	}

	@Override
	public boolean getRequiresAddress() {
		return true;
	}

	@Override
	public String getInfo(Locale locale) {
		return "Tuotteet toimitetaan postitse";
	}
	
}
