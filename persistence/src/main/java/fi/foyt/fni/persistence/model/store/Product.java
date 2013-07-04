package fi.foyt.fni.persistence.model.store;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.foyt.fni.persistence.model.common.MultilingualString;
import fi.foyt.fni.persistence.model.users.User;

@Entity
@Inheritance (strategy=InheritanceType.JOINED)
public class Product {

  public Long getId() {
    return id;
  }
  
  public Boolean getPublished() {
		return published;
	}
  
  public void setPublished(Boolean published) {
		this.published = published;
	}

  public MultilingualString getName() {
		return name;
	}
  
  public void setName(MultilingualString name) {
		this.name = name;
	}
  
  public MultilingualString getDescription() {
		return description;
	}
  
  public void setDescription(MultilingualString description) {
		this.description = description;
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
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotNull
  @Column (nullable = false, columnDefinition = "BIT")
  private Boolean published;

  @OneToOne
  private MultilingualString name;
  
  @OneToOne
  private MultilingualString description;

  @NotNull
  @Column(nullable = false)
  private Double price;
  
  @ManyToOne
  private ProductImage defaultImage;
  
  @NotNull
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;

  @NotNull
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne
  private User creator;

  @ManyToOne
  private User modifier;
}
