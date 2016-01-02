package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class IllusionEventRegistrationForm {

  public Long getId() {
    return id;
  }
  
  public IllusionEvent getEvent() {
    return event;
  }
  
  public void setEvent(IllusionEvent event) {
    this.event = event;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private IllusionEvent event;

  @Lob
  private String data;
}