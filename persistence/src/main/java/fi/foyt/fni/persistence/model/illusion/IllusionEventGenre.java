package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class IllusionEventGenre {

  public Long getId() {
    return id;
  }
  
  public IllusionEvent getEvent() {
    return event;
  }
  
  public void setEvent(IllusionEvent event) {
    this.event = event;
  }
  
  public Genre getGenre() {
    return genre;
  }
  
  public void setGenre(Genre genre) {
    this.genre = genre;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private IllusionEvent event;
  
  @ManyToOne
  private Genre genre;
}