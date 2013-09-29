package fi.foyt.fni.view.gamelibrary.old;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.User;

//@RequestScoped
//@Named
//@Stateful
public class AbstractPublicationListBackingBean {
	
/**
	protected void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
	
	public List<Publication> getPublications() {
		return publications;
	}
	
	public BookPublication getBookPublication(Publication publication) {
		if (publication instanceof BookPublication) {
			return (BookPublication) publication;
		}
		
		return null;
	}
	
	public List<PublicationImage> getPublicationImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication);
	}


	public boolean hasSeveralImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication).size() > 1;
	}
	
	public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}
	
	private List<Publication> publications;
	
	**/
}