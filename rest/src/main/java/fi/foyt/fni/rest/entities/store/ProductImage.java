package fi.foyt.fni.rest.entities.store;

import java.util.Date;

import fi.foyt.fni.rest.entities.users.User;

public class ProductImage {

	public ProductImage(Long id, Date modified, Date created, User creator, User modifier, String downloadUrl) {
		super();
		this.id = id;
		this.modified = modified;
		this.created = created;
		this.creator = creator;
		this.modifier = modifier;
		this.downloadUrl = downloadUrl;
	}

	public Long getId() {
		return id;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getModifier() {
		return modifier;
	}

	public void setModifier(User modifier) {
		this.modifier = modifier;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	private Long id;

	private Date modified;

	private Date created;

	private User creator;

	private User modifier;

	private String downloadUrl;
}
