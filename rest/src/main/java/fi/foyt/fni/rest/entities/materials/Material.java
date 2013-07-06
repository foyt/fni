package fi.foyt.fni.rest.entities.materials;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.foyt.fni.rest.entities.users.User;

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
  
  public String getPublicity() {
	  return publicity;
  }
  
  public void setPublicity(String publicity) {
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
  
  public String getType() {
    return type;
  }
  
  protected void setType(String type) {
    this.type = type;
  }
  
  public String getLanguage() {
    return language;
  }
  
  public void setLanguage(String language) {
    this.language = language;
  }
  
  public Boolean getStarred() {
		return starred;
	}
  
  public void setStarred(Boolean starred) {
		this.starred = starred;
	}
  
  public String getRole() {
		return role;
	}
  
  public void setRole(String role) {
		this.role = role;
	}
  
  public Folder getParentFolder() {
    return parentFolder;
  }
  
  public void setParentFolder(Folder parentFolder) {
    this.parentFolder = parentFolder;
  }
  
  public List<String> getTags() {
		return tags;
	}
  
  public void setTags(List<String> tags) {
		this.tags = tags;
	}
  
  public List<String> getPermaLinks() {
		return permaLinks;
	}
  
  public void setPermaLinks(List<String> permaLinks) {
		this.permaLinks = permaLinks;
	}
  
  public Map<String, String> getExportLinks() {
		return exportLinks;
	}
  
  public void setExportLinks(Map<String, String> exportLinks) {
		this.exportLinks = exportLinks;
	}

  private Long id;
  
  private String type;
  
  private String urlName;
  
  private String title;
  
  private String publicity;
  
  private String language;
  
  private Date modified;
  
  private Date created;

  private User creator;
  
  private User modifier;
  
  private Folder parentFolder;
  
  private Boolean starred;
  
  private String role;
  
  private List<String> tags;
  
  private List<String> permaLinks;
  
  private Map<String, String> exportLinks;
}