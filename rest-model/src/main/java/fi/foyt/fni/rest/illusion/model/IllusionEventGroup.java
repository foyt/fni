package fi.foyt.fni.rest.illusion.model;

public class IllusionEventGroup {

  public IllusionEventGroup() {
  }
  
  public IllusionEventGroup(Long id, String name, Long eventId) {
    this.id = id;
    this.name = name;
    this.eventId = eventId;
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
  
  public Long getEventId() {
    return eventId;
  }
  
  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }
  
  private Long id;
  private String name;
  private Long eventId;
}
