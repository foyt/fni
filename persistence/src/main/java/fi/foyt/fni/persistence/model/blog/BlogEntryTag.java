package fi.foyt.fni.persistence.model.blog;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Cacheable (true)
public class BlogEntryTag {

  public Long getId() {
    return id;
  }

  public BlogEntry getEntry() {
		return entry;
	}
  
  public void setEntry(BlogEntry entry) {
		this.entry = entry;
	}
  
  public BlogTag getTag() {
		return tag;
	}
  
  public void setTag(BlogTag tag) {
		this.tag = tag;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private BlogEntry entry;
  
  @ManyToOne
  private BlogTag tag;
}