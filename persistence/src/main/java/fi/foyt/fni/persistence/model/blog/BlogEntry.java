package fi.foyt.fni.persistence.model.blog;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
public class BlogEntry {

  public Long getId() {
    return id;
  }
  
  public String getGuid() {
		return guid;
	}
  
  public void setGuid(String guid) {
		this.guid = guid;
	}
  
  public BlogCategory getCategory() {
		return category;
	}
  
  public void setCategory(BlogCategory category) {
		this.category = category;
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
  
  public String getSummary() {
		return summary;
	}
  
  public void setSummary(String summary) {
		this.summary = summary;
	}
  
  public String getContent() {
		return content;
	}
  
  public void setContent(String content) {
		this.content = content;
	}
  
  public String getLink() {
		return link;
	}
  
  public void setLink(String link) {
		this.link = link;
	}
  
  public String getAuthorName() {
		return authorName;
	}
  
  public void setAuthorName(String authorName) {
		this.authorName = authorName;
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
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @NotNull
  @NotEmpty
  @Column (nullable=false, unique = true)
  private String guid;

  @ManyToOne
  private BlogCategory category;
  
  private String urlName;
  
  @NotNull
  @NotEmpty
  @Column (nullable=false)
  private String title;

  @NotNull
  @NotEmpty
  @Column (nullable=false)
  @Lob
  private String summary;
  
  @Lob
  private String content;
  
  private String link;
  
  private String authorName;
  
  @NotNull
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date modified;
  
  @NotNull
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne
  private User creator;
  
  @ManyToOne
  private User modifier;
}