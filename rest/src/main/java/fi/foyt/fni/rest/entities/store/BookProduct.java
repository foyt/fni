package fi.foyt.fni.rest.entities.store;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.foyt.fni.rest.entities.users.User;

public class BookProduct extends FileProduct {
	
	public BookProduct() {
	}

	public BookProduct(Long id, Boolean published, String type, Map<Locale, String> names, Map<Locale, String> descriptions, Double price,
			ProductImage defaultImage, Date modified, Date created, User creator, User modifier, Boolean requiresDelivery, List<String> tags, Map<String, String> details, String downloadUrl,
		  Boolean downloadable) {
		super(id, published, type, names, descriptions, price, defaultImage, modified, created, creator, modifier, requiresDelivery, tags, details, downloadUrl);
		this.downloadable = downloadable;
	}

	public Boolean getDownloadable() {
		return downloadable;
	}

	public void setDownloadable(Boolean downloadable) {
		this.downloadable = downloadable;
	}

	private Boolean downloadable;
}
