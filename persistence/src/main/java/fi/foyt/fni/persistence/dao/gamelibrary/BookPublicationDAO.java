package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationFile;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class BookPublicationDAO extends GenericDAO<BookPublication> {
  
	private static final long serialVersionUID = 1L;

	public BookPublication create(String name, String urlName, String description, Double price, Boolean downloadable, Boolean purchasable, PublicationImage defaultImage, Date created, User creator, Date modified, User modifier, Boolean published, Boolean requiresDelivery, Integer height, Integer width, Integer depth, Double weight, String author, Integer numberOfPages, ForumTopic forumTopic) {
    
		BookPublication bookPublication = new BookPublication();
    bookPublication.setCreated(created);
    bookPublication.setCreator(creator);
    bookPublication.setDefaultImage(defaultImage);
    bookPublication.setDescription(description);
    bookPublication.setDownloadable(downloadable);
    bookPublication.setModified(modified);
    bookPublication.setModifier(modifier);
    bookPublication.setName(name);
    bookPublication.setUrlName(urlName);
    bookPublication.setPrice(price);
    bookPublication.setPublished(published);
    bookPublication.setRequiresDelivery(requiresDelivery);
    bookPublication.setPurchasable(purchasable);
    bookPublication.setHeight(height);
    bookPublication.setWidth(width);
    bookPublication.setDepth(depth);
    bookPublication.setWeight(weight);
    bookPublication.setAuthor(author);
    bookPublication.setNumberOfPages(numberOfPages);
    bookPublication.setForumTopic(forumTopic);
    
    getEntityManager().persist(bookPublication);
    
    return bookPublication;
	}

	public BookPublication updateDownloadable(BookPublication bookPublication, Boolean downloadable) {
		bookPublication.setDownloadable(downloadable);
		getEntityManager().persist(bookPublication);
    return bookPublication;
	}

	public BookPublication updateAuthor(BookPublication bookPublication, String author) {
		bookPublication.setAuthor(author);
		getEntityManager().persist(bookPublication);
    return bookPublication;
	}

	public BookPublication updateNumberOfPages(BookPublication bookPublication, Integer numberOfPages) {
		bookPublication.setNumberOfPages(numberOfPages);
		getEntityManager().persist(bookPublication);
    return bookPublication;
	}

	public BookPublication updateFile(BookPublication bookPublication, PublicationFile file) {
		bookPublication.setFile(file);
		getEntityManager().persist(bookPublication);
		return bookPublication;
	}
  
}
