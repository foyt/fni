package fi.foyt.fni.rest.entities.store;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import fi.foyt.fni.rest.entities.users.User;

@JsonTypeInfo (
	include = As.PROPERTY,
  property = "type",
  use = Id.NAME
)
@JsonSubTypes ({
  @JsonSubTypes.Type(name = "BOOK", value = BookProduct.class),
	@JsonSubTypes.Type(name = "PREMIUM_ACCOUNT", value = PremiumAccountProduct.class)
})
public class Product {
	
	public Product() {
	}

	public Product(Long id, Boolean published, String type, Map<Locale, String> names, Map<Locale, String> descriptions, Double price, ProductImage defaultImage,
			Date modified, Date created, User creator, User modifier, Boolean requiresDelivery, List<String> tags, Map<String, String> details) {
		super();
		this.id = id;
		this.published = published;
		this.type = type;
		this.names = names;
		this.descriptions = descriptions;
		this.price = price;
		this.defaultImage = defaultImage;
		this.modified = modified;
		this.created = created;
		this.creator = creator;
		this.requiresDelivery = requiresDelivery;
		this.modifier = modifier;
		this.tags = tags;
		this.details = details;
	}

	public Long getId() {
		return id;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<Locale, String> getNames() {
		return names;
	}

	public void setNames(Map<Locale, String> names) {
		this.names = names;
	}

	public Map<Locale, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<Locale, String> descriptions) {
		this.descriptions = descriptions;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public ProductImage getDefaultImage() {
		return defaultImage;
	}

	public void setDefaultImage(ProductImage defaultImage) {
		this.defaultImage = defaultImage;
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
	
	public Boolean getRequiresDelivery() {
		return requiresDelivery;
	}
	
	public void setRequiresDelivery(Boolean requiresDelivery) {
		this.requiresDelivery = requiresDelivery;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}

	private Long id;

	private Boolean published;

	private String type;

	private Map<Locale, String> names;

	private Map<Locale, String> descriptions;

	private Double price;

	private ProductImage defaultImage;

	private Date modified;

	private Date created;

	private User creator;

	private User modifier;
	
	private Boolean requiresDelivery;
	
	private List<String> tags;

	private Map<String, String> details;
}