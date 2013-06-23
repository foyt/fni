package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;

import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Inheritance (strategy=InheritanceType.JOINED)
public class Material {

  public Long getId() {
    return id;
  }
  
  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public MaterialPublicity getPublicity() {
	  return publicity;
  }
  
  public void setPublicity(MaterialPublicity publicity) {
	  this.publicity = publicity;
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
  
  public MaterialType getType() {
    return type;
  }
  
  protected void setType(MaterialType type) {
    this.type = type;
  }
  
  public Language getLanguage() {
    return language;
  }
  
  public void setLanguage(Language language) {
    this.language = language;
  }
  
  public Folder getParentFolder() {
    return parentFolder;
  }
  
  public void setParentFolder(Folder parentFolder) {
    this.parentFolder = parentFolder;
  }
  
  @Transient
  public String getPath() {
    if (getParentFolder() != null)
      return getParentFolder().getPath() + '/' + getUrlName();
    else {
      return "materials/" + getCreator().getId().toString() + '/' + getUrlName();
    }
  }
  
  @Id
  @DocumentId
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (updatable = false, nullable = false)
  @Enumerated (EnumType.STRING)
  private MaterialType type;
  
  @Column (nullable=false)
  private String urlName;
  
  @Column (nullable=false)
  @Field
  private String title;
  
  @Column (nullable=false)
  @Enumerated (EnumType.STRING)
  private MaterialPublicity publicity;
  
  @ManyToOne
  private Language language;
  
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date modified;
  
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne
  private User creator;
  
  @ManyToOne
  private User modifier;
  
  @ManyToOne
  private Folder parentFolder;
}