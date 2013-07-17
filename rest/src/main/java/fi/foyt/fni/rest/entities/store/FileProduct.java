package fi.foyt.fni.rest.entities.store;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.foyt.fni.rest.entities.users.User;

public class FileProduct extends Product {
	
	public FileProduct() {
	}

	public FileProduct(Long id, Boolean published, String type, String name, String description, Double price,
			ProductImage defaultImage, Date modified, Date created, User creator, User modifier, Boolean requiresDelivery, List<String> tags, Map<String, String> details, String downloadUrl) {
		super(id, published, type, name, description, price, defaultImage, modified, created, creator, modifier, requiresDelivery, tags, details);
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
