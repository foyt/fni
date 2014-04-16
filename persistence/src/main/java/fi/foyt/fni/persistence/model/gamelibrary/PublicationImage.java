package fi.foyt.fni.persistence.model.gamelibrary;

import java.util.Date;

import javax.persistence.Cacheable;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PublicationImage {

  public Long getId() {
    return id;
  }
  
  public Publication getPublication() {
		return publication;
	}
  
  public void setPublication(Publication publication) {
		this.publication = publication;
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
  private Publication publication;
  
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
