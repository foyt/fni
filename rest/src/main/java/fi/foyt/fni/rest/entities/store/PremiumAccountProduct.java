package fi.foyt.fni.rest.entities.store;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.foyt.fni.rest.entities.users.User;

public class PremiumAccountProduct extends Product {
	
	public PremiumAccountProduct() {
	}
	
	public PremiumAccountProduct(Long id, Boolean published, String type, String name, String description, Double price,
			ProductImage defaultImage, Date modified, Date created, User creator, User modifier, Boolean requiresDelivery,  List<String> tags, Map<String, String> details, Integer months) {
		super(id, published, type, name, description, price, defaultImage, modified, created, creator, modifier, requiresDelivery, tags, details);
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
