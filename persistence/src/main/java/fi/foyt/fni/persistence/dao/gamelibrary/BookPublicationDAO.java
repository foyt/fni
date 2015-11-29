package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationFile;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.Publication_;
import fi.foyt.fni.persistence.model.users.User;

public class BookPublicationDAO extends GenericDAO<BookPublication> {
  
	private static final long serialVersionUID = 1L;

	public BookPublication create(String name, String urlName, String description, Double price, Double authorsShare, PublicationImage defaultImage, Date created, User creator, Date modified, User modifier, Boolean published, Integer height, Integer width, Integer depth, Double weight, Integer numberOfPages, String license, ForumTopic forumTopic, Language language, Long downloadCount, Long printCount) {
    
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
    bookPublication.setAuthorsShare(authorsShare);
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

  public List<BookPublication> listByPublished(Boolean published) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BookPublication> criteria = criteriaBuilder.createQuery(BookPublication.class);
    Root<BookPublication> root = criteria.from(BookPublication.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Publication_.published), published)
    );
    
    return entityManager.createQuery(criteria).getResultList();
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

  public BookPublication updateAuthorsShare(BookPublication bookPublication, Double authorsShare) {
    bookPublication.setAuthorsShare(authorsShare);
    return persist(bookPublication);
  }

}
