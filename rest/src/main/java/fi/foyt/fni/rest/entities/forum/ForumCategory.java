package fi.foyt.fni.rest.entities.forum;

public class ForumCategory {

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  private Long id;
  
  private String name;
}
