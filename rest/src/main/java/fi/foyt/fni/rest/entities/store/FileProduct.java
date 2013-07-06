package fi.foyt.fni.rest.entities.store;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.foyt.fni.rest.entities.users.User;

public class FileProduct extends Product {
	
	public FileProduct() {
	}

	public FileProduct(Long id, Boolean published, String type, Map<Locale, String> names, Map<Locale, String> descriptions, Double price,
			ProductImage defaultImage, Date modified, Date created, User creator, User modifier, List<String> tags, Map<String, String> details, String downloadUrl) {
		super(id, published, type, names, descriptions, price, defaultImage, modified, created, creator, modifier, tags, details);
		this.downloadUrl = downloadUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	private String downloadUrl;
}
