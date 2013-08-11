package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class PublicationAuthor {

	public Long getId() {
		return id;
	}
	
	public Publication getPublication() {
		return publication;
	}
	
	public void setPublication(Publication publication) {
		this.publication = publication;
	}

	public User getAuthor() {
		return author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

  @ManyToOne
  private Publication publication;

  @ManyToOne
  private User author;
}
