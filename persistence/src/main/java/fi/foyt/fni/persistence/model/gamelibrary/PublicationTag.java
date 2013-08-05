package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PublicationTag {

  public Long getId() {
    return id;
  }
  
  public Publication getPublication() {
		return publication;
	}
  
  public void setPublication(Publication publication) {
		this.publication = publication;
	}
  
  public GameLibraryTag getTag() {
		return tag;
	}
  
  public void setTag(GameLibraryTag tag) {
		this.tag = tag;
	}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Publication publication;
  
  @ManyToOne
  private GameLibraryTag tag;
}
