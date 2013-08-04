package fi.foyt.fni.persistence.model.gamelibrary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class ProductImage {

  public Long getId() {
    return id;
  }
  
  public Product getProduct() {
		return product;
	}
  
  public void setProduct(Product product) {
		this.product = product;
	}
  
  public String getContentType() {
		return contentType;
	}
  
  public void setContentType(String contentType) {
		this.contentType = contentType;
	}

  public byte[] getContent() {
		return content;
	}
  
  public void setContent(byte[] content) {
		this.content = content;
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

  @OneToOne
  private Product product;
  
  @Column(nullable = false)
  @NotNull
  @NotEmpty
  private String contentType;

  @Column(nullable = false)
  @NotNull
  @Lob
  private byte[] content;
  
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
