package fi.foyt.fni.rest.entities.forum;

public class Forum {

  public Long getId() {
    return id;
  }
  
  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public ForumCategory getCategory() {
    return category;
  }
  
  public void setCategory(ForumCategory category) {
    this.category = category;
  }
  
  private Long id;
  
  private String urlName;
  
  private String name;
  
  private String description;
  
  private ForumCategory category;
}
