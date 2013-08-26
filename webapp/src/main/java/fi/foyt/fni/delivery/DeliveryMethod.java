package fi.foyt.fni.delivery;

import java.util.Currency;

public interface DeliveryMethod {
	
	/**
	 * Returns unique identifier of the delivery method
	 * 
	 * @return unique identifier of the delivery method
	 */
	public String getId();

	/**
	 * Returns price of delivery based on weight, dimensions and delivery country.
	 * 
	 * @param weight package weight in grams
	 * @param width package width in millimeters
	 * @param height package height in millimeters
	 * @param depth package depth in millimeters
	 * @param countryCode Country code in ISO 3166-1 alpha-2 format.
	 * @return price in currency returned by getCurrency method
	 */
	public Double getPrice(Double weight, int width, int height, int depth, String countryCode);
	
	/**
	 * Returns currency DeliveryMethod uses
	 * 
	 * @return currency used by DeliveryMehod
	 */
	public Currency getCurrency();
	
	/**
	 * Returns whether delivery method requires an address
	 * 
	 * @return whether delivery method requires an address
	 */
	public boolean getRequiresAddress();
	
	/**
	 * Returns locale key for delivery method name
	 * 
	 * @param weight package weight 
	 * @param width package width
	 * @param height package height
	 * @param depth package depth
	 * @param countryCode target country code
	 * @return locale key
	 */
	public String getNameLocaleKey(Double weight, int width, int height, int depth, String countryCode);
	
	/**
	 * Returns locale key for delivery method info
	 * 
	 * @param weight package weight 
	 * @param width package width
	 * @param height package height
	 * @param depth package depth
	 * @param countryCode target country code
	 * @return locale key
	 */
	public String getInfoLocaleKey(Double weight, int width, int height, int depth, String countryCode);
}
