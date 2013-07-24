package fi.foyt.fni.persistence.dao.store;

import java.util.Date;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class BookProductDAO extends GenericDAO<BookProduct> {
  
	private static final long serialVersionUID = 1L;

	public BookProduct create(String name, String description, Double price, Boolean downloadable, Boolean purchasable, ProductImage defaultImage, Date created, User creator, Date modified, User modifier, Boolean published, Boolean requiresDelivery, Integer height, Integer width, Integer depth, Double weight, String author, Integer numberOfPages, ForumTopic forumTopic) {
    
		BookProduct bookProduct = new BookProduct();
    bookProduct.setCreated(created);
    bookProduct.setCreator(creator);
    bookProduct.setDefaultImage(defaultImage);
    bookProduct.setDescription(description);
    bookProduct.setDownloadable(downloadable);
    bookProduct.setModified(modified);
    bookProduct.setModifier(modifier);
    bookProduct.setName(name);
    bookProduct.setPrice(price);
    bookProduct.setPublished(published);
    bookProduct.setRequiresDelivery(requiresDelivery);
    bookProduct.setPurchasable(purchasable);
    bookProduct.setHeight(height);
    bookProduct.setWidth(width);
    bookProduct.setDepth(depth);
    bookProduct.setWeight(weight);
    bookProduct.setAuthor(author);
    bookProduct.setNumberOfPages(numberOfPages);
    bookProduct.setForumTopic(forumTopic);
    
    getEntityManager().persist(bookProduct);
    
    return bookProduct;
	}

	public BookProduct updateDownloadable(BookProduct bookProduct, Boolean downloadable) {
		bookProduct.setDownloadable(downloadable);
		getEntityManager().persist(bookProduct);
    return bookProduct;
	}

	public BookProduct updateAuthor(BookProduct bookProduct, String author) {
		bookProduct.setAuthor(author);
		getEntityManager().persist(bookProduct);
    return bookProduct;
	}

	public BookProduct updateNumberOfPages(BookProduct bookProduct, Integer numberOfPages) {
		bookProduct.setNumberOfPages(numberOfPages);
		getEntityManager().persist(bookProduct);
    return bookProduct;
	}
	
}
