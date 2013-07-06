package fi.foyt.fni.rest.entities.store;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.foyt.fni.rest.entities.users.User;

public class PremiumAccountProduct extends Product {
	
	public PremiumAccountProduct() {
	}
	
	public PremiumAccountProduct(Long id, Boolean published, String type, Map<Locale, String> name, Map<Locale, String> descriptions, Double price,
			ProductImage defaultImage, Date modified, Date created, User creator, User modifier, List<String> tags, Map<String, String> details, Integer months) {
		super(id, published, type, name, descriptions, price, defaultImage, modified, created, creator, modifier, tags, details);
		this.months = months;
	}

	public Integer getMonths() {
		return months;
	}
	
	public void setMonths(Integer months) {
		this.months = months;
	}
	
	private Integer months;
}
