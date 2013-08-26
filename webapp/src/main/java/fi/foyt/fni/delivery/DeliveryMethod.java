package fi.foyt.fni.delivery;

import java.util.Currency;
import java.util.Locale;

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
	 * Returns localized name of the delivery method
	 * 
	 * @param locale locale in which name is returned 
	 * @return localized name of the delivery method
	 */
	public String getName(Locale locale);
	
	/**
	 * Returns localized info of the delivery method
	 * 
	 * @param locale locale in which name is returned 
	 * @return localized info of the delivery method
	 */
	public String getInfo(Locale locale);
}
