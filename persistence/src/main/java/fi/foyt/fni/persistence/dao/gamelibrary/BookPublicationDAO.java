package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationFile;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class BookPublicationDAO extends GenericDAO<BookPublication> {
  
	private static final long serialVersionUID = 1L;

	public BookPublication create(String name, String urlName, String description, Double price, PublicationImage defaultImage, Date created, User creator, Date modified, User modifier, Boolean published, Integer height, Integer width, Integer depth, Double weight, Integer numberOfPages, String license, ForumTopic forumTopic, Language language, Long downloadCount, Long printCount) {
    
		BookPublication bookPublication = new BookPublication();
    bookPublication.setCreated(created);
    bookPublication.setCreator(creator);
    bookPublication.setDefaultImage(defaultImage);
    bookPublication.setDescription(description);
    bookPublication.setModified(modified);
    bookPublication.setModifier(modifier);
    bookPublication.setName(name);
    bookPublication.setUrlName(urlName);
    bookPublication.setPrice(price);
    bookPublication.setPublished(published);
    bookPublication.setHeight(height);
    bookPublication.setWidth(width);
    bookPublication.setDepth(depth);
    bookPublication.setWeight(weight);
    bookPublication.setNumberOfPages(numberOfPages);
    bookPublication.setLicense(license);
    bookPublication.setForumTopic(forumTopic);
    bookPublication.setLanguage(language);
    bookPublication.setDownloadCount(downloadCount);
    bookPublication.setPrintCount(printCount);
    
    getEntityManager().persist(bookPublication);
    
    return bookPublication;
	}

	public BookPublication updateNumberOfPages(BookPublication bookPublication, Integer numberOfPages) {
		bookPublication.setNumberOfPages(numberOfPages);
		getEntityManager().persist(bookPublication);
    return bookPublication;
	}

  public BookPublication updateDownlodableFile(BookPublication bookPublication, PublicationFile downloadableFile) {
    bookPublication.setDownloadableFile(downloadableFile);
    return persist(bookPublication);
  }

  public BookPublication updatePrintableFile(BookPublication bookPublication, PublicationFile printableFile) {
    bookPublication.setPrintableFile(printableFile);
    return persist(bookPublication);
  }

  public BookPublication updateDownloadCount(BookPublication bookPublication, Long downloadCount) {
    bookPublication.setDownloadCount(downloadCount);
    return persist(bookPublication);
  }

  public BookPublication updatePrintCount(BookPublication bookPublication, Long printCount) {
    bookPublication.setPrintCount(printCount);
    return persist(bookPublication);
  }

}
