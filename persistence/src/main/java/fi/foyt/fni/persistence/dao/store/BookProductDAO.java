package fi.foyt.fni.persistence.dao.store;

import java.util.Date;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class BookProductDAO extends GenericDAO<BookProduct> {
  
	public BookProduct create(String name, String description, Double price, Boolean downloadable, ProductImage defaultImage, Date created, User creator, Date modified, User modifier, Boolean published, Boolean requiresDelivery) {
    
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
    
    getEntityManager().persist(bookProduct);
    
    return bookProduct;
	}

	public BookProduct updateDownloadable(BookProduct bookProduct, Boolean downloadable) {
		bookProduct.setDownloadable(downloadable);
		getEntityManager().persist(bookProduct);
    return bookProduct;
	}
	
}
