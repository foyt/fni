package fi.foyt.fni.persistence.model.blog;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable (true)
public class BlogCategory {

  public Long getId() {
    return id;
  }
  
  public String getName() {
		return name;
	}
  
  public void setName(String name) {
		this.name = name;
	}
  
  public Date getNextSync() {
		return nextSync;
	}
  
  public void setNextSync(Date nextSync) {
		this.nextSync = nextSync;
	}
  
  public BlogCategorySync getSync() {
		return sync;
	}
  
  public void setSync(BlogCategorySync sync) {
		this.sync = sync;
	}
  
  public String getSyncUrl() {
		return syncUrl;
	}
  
  public void setSyncUrl(String syncUrl) {
		this.syncUrl = syncUrl;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @NotNull
  @NotEmpty
  @Column (nullable=false)
  private String name;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date nextSync; 
  
  private String syncUrl;
  
  @NotNull
  @Column (nullable=false)
  @Enumerated (EnumType.STRING)
  private BlogCategorySync sync;
}