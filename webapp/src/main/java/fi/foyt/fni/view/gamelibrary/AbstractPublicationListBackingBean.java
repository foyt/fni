package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

public class AbstractPublicationListBackingBean {
	
	@Inject
	private PublicationController publicationController;
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private ForumController forumController;

	@Inject
	private ShoppingCartController shoppingCartController;

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
	
	public Long getPublicationCommentCount(Publication publication) {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
	}
	
	public List<GameLibraryTag> getTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<>();

		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}

		return result;
	}
	
	public List<PublicationImage> getPublicationImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication);
	}
	
	public PublicationImage getFirstImage(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication).get(0);
	}

	public boolean hasImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication).size() > 0;
	}

	public boolean hasSeveralImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication).size() > 1;
	}
	
	public void addPublicationToShoppingCart(Publication publication) {
		shoppingCartController.addPublication(publication);
	}
	
	public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}
	
	private List<Publication> publications;
}