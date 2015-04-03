package fi.foyt.fni.rest.illusion.model;

public class Genre {
  
  public Genre() {
  }
  
  public Genre(Long id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
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
