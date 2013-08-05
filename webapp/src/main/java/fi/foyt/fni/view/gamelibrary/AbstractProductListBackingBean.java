package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

public class AbstractProductListBackingBean {
	
	@Inject
	private ProductController productController;
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private ForumController forumController;

	@Inject
	private ShoppingCartController shoppingCartController;

	protected void setProducts(List<Publication> publications) {
		this.publications = publications;
	}
	
	public List<Publication> getProducts() {
		return publications;
	}
	
	public BookPublication getBookProduct(Publication publication) {
		if (publication instanceof BookPublication) {
			return (BookPublication) publication;
		}
		
		return null;
	}
	
	public Long getProductCommentCount(Publication publication) {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
	}
	
	public List<GameLibraryTag> getTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<>();

		List<PublicationTag> publicationTags = gameLibraryTagController.listProductTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}

		return result;
	}
	
	public List<PublicationImage> getProductImages(Publication publication) {
		return productController.listProductImageByProduct(publication);
	}
	
	public PublicationImage getFirstImage(Publication publication) {
		return productController.listProductImageByProduct(publication).get(0);
	}

	public boolean hasImages(Publication publication) {
		return productController.listProductImageByProduct(publication).size() > 0;
	}

	public boolean hasSeveralImages(Publication publication) {
		return productController.listProductImageByProduct(publication).size() > 1;
	}
	
	public void addProductToShoppingCart(Publication publication) {
		shoppingCartController.addProduct(publication);
	}
	
	private List<Publication> publications;
}