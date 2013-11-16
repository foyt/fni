package fi.foyt.fni.gamelibrary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.dao.gamelibrary.BookPublicationDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationFileDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationAuthorDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationImageDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationTagDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationFile;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Stateful
@Dependent
public class PublicationController {
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private PublicationDAO publicationDAO;

	@Inject
	private PublicationTagDAO publicationTagDAO;

	@Inject
	private PublicationImageDAO publicationImageDAO;

	@Inject
	private BookPublicationDAO bookPublicationDAO;

	@Inject
	private PublicationFileDAO publicationFileDAO;

	@Inject
	private PublicationAuthorDAO publicationAuthorDAO;

	@Inject
	private ForumController forumController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	@Inject
	private FullTextEntityManager fullTextEntityManager;
	
	/* Publications */

	public Publication findPublicationById(Long id) {
		return publicationDAO.findById(id);
	}

	public Publication findPublicationByUrlName(String urlName) {
		return publicationDAO.findByUrlName(urlName);
	}

	public List<Publication> listAllPublications() {
		return publicationDAO.listAll();
	}

	public List<Publication> listPublicationsByTags(String... tags) {
		List<GameLibraryTag> gameLibraryTags = new ArrayList<>();

		for (String tag : tags) {
			gameLibraryTags.add(gameLibraryTagController.findTagByText(tag));
		}

		return listPublicationsByTags(gameLibraryTags);
	}

	public List<Publication> listPublicationsByTags(List<GameLibraryTag> gameLibraryTags) {
		return publicationTagDAO.listPublicationsByGameLibraryTags(gameLibraryTags);
	}

	public List<Publication> listRecentPublications(int maxRecentPublication) {
		return publicationDAO.listByPublishedOrderByCreated(Boolean.TRUE, 0, maxRecentPublication);
	}

	public List<Publication> listUnpublishedPublications() {
		return publicationDAO.listByPublished(Boolean.FALSE);
	}
	
	public List<Publication> listPublishedPublications() {
		return publicationDAO.listByPublished(Boolean.TRUE);
	}
	
	public List<Publication> listPublishedPublicationsByCreator(User creator) {
		return publicationDAO.listByCreatorAndPublished(creator, Boolean.TRUE);
	}
	
	public List<Publication> listPublicationsByAuthor(User author) {
		return publicationAuthorDAO.listPublicationsByAuthor(author);
	}

	public List<Publication> listPublishedPublicationsByAuthor(User author) {
		List<Publication> result = new ArrayList<>();
		List<Publication> publications = listPublicationsByAuthor(author);
		for (Publication publication : publications) {
			if (publication.getPublished()) {
				result.add(publication);
			}
		}
		
		return result;
	}

	public Long countUnpublishedPublicationsByCreator(User user) {
		return publicationDAO.countByCreatorAndPublished(user, Boolean.FALSE);
	}
	
	public List<SearchResult<Publication>> searchPublications(String text) throws ParseException {
	  return searchPublications(text, null);
	}
	
	public List<SearchResult<Publication>> searchPublications(String text, Integer maxHits) throws ParseException {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		
		List<SearchResult<Publication>> result = new ArrayList<>();
		
		String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");

		StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append("+(");
		for (int i = 0, l = criterias.length; i < l; i++) {
			String criteria = QueryParser.escape(criterias[i]);
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("*");
			if (i < l - 1)
			  queryStringBuilder.append(' ');
		}
		queryStringBuilder.append(") AND +(published:true)");
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser parser = new QueryParser(Version.LUCENE_35, "descriptionPlain", analyzer);
		Query luceneQuery = parser.parse(queryStringBuilder.toString());
    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, BookPublication.class);
		@SuppressWarnings("unchecked")
		List<Publication> searchResults = query.getResultList();
    for (Publication searchResult : searchResults) {
    	String link = new StringBuilder()
    	  .append("/gamelibrary/")
    	  .append(searchResult.getUrlName())
    	  .toString();
    	result.add(new SearchResult<Publication>(searchResult, searchResult.getName(), link, null, null, null));
    	
    	if (maxHits != null && result.size() >= maxHits) {
    		return result;
    	}
    }
		
		return result;
	}
	
	public Publication updatedModified(Publication publication, User modifier, Date modified) {
		publicationDAO.updateModified(publication, modified);
		publicationDAO.updateModifier(publication, modifier);
		
		return publication;
	}

	public Publication updateLicense(Publication publication, String licenseUrl) {
		return publicationDAO.updateLicense(publication, licenseUrl);
	}
	
	public Publication updateDimensions(Publication publication, Integer width, Integer height, Integer depth) {
		publicationDAO.updateWidth(publication, width);
		publicationDAO.updateHeight(publication, height);
		publicationDAO.updateDepth(publication, depth);
		
		return publication;
	}
	
	public Publication updateWeight(Publication publication, Double weight) {
		return publicationDAO.updateWeight(publication, weight);
	}
	
	public Publication updatePublished(Publication publication, Boolean published) {
		return publicationDAO.updatePublished(publication, published);
	}

	public Publication updatePrice(Publication publication, Double price) {
		return publicationDAO.updatePrice(publication, price);
	}

	public Publication updateDescription(Publication publication, String description) {
		return publicationDAO.updateDescription(publication, description);
	}

	public Publication updateName(Publication publication, String name) {
	  if (!name.equals(publication.getName())) {
      String urlName = createUrlName(publication, name);
 	    publicationDAO.updateUrlName(publication, urlName);
		  return publicationDAO.updateName(publication, name);
	  }
	  
	  return publication;
	}

	public Publication updatePublicationAuthors(Publication publication, List<User> authors) {
		for (User author : authors) {
			if (publicationAuthorDAO.findByPublicationAndAuthor(publication, author) == null) {
				publicationAuthorDAO.create(publication, author);
			}
		}
		
		List<PublicationAuthor> removeAuthors = null;
		if (authors.size() > 0) {
			removeAuthors = publicationAuthorDAO.listByPublicationAndAuthorNotIn(publication, authors);
		} else {
			removeAuthors = publicationAuthorDAO.listByPublication(publication);
		}
		
		for (PublicationAuthor removeAuthor : removeAuthors) {
			publicationAuthorDAO.delete(removeAuthor);
		}
		
		return publication;
	}

	public Publication updateTags(Publication publication, List<GameLibraryTag> tags) {
		List<GameLibraryTag> addTags = new ArrayList<>(tags);
		
		Map<Long, PublicationTag> existingTagMap = new HashMap<Long, PublicationTag>();
		List<PublicationTag> existingTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag existingTag : existingTags) {
			existingTagMap.put(existingTag.getTag().getId(), existingTag);
		}
		
		for (int i = addTags.size() - 1; i >= 0; i--) {
			GameLibraryTag addTag = addTags.get(i);
			
			if (existingTagMap.containsKey(addTag.getId())) {
				addTags.remove(i);
			} 
			
			existingTagMap.remove(addTag.getId());
		}
		
		for (PublicationTag removeTag : existingTagMap.values()) {
			gameLibraryTagController.deletePublicationTag(removeTag);
		}
		
		for (GameLibraryTag gameLibraryTag : addTags) {
			publicationTagDAO.create(gameLibraryTag, publication);
		}
		
		return publication;
	}
	
	public Publication publishPublication(Publication publication) {
		return publicationDAO.updatePublished(publication, Boolean.TRUE);
	}

	public Publication unpublishPublication(Publication publication) {
		return publicationDAO.updatePublished(publication, Boolean.FALSE);
	}
	
	public Publication updatePublicationDefaultImage(Publication publication, PublicationImage publicationImage) {
		return publicationDAO.updateDefaultImage(publication, publicationImage);
	}

	public Publication updatePublicationForumTopic(Publication publication, ForumTopic forumTopic) {
		return publicationDAO.updateForumTopic(publication, forumTopic);
	}
	
	public void deletePublication(Publication publication) {
		for (PublicationImage publicationImage : listPublicationImagesByPublication(publication)) {
			deletePublicationImage(publicationImage);
		}
		
		if (publication instanceof BookPublication) {
      deleteBookPublicationDownloableFile((BookPublication) publication);
      deleteBookPublicationPrintableFile((BookPublication) publication);
		}
		
		for (PublicationTag publicationTag : gameLibraryTagController.listPublicationTags(publication)) {
			gameLibraryTagController.deletePublicationTag(publicationTag);
		}
		
		List<PublicationAuthor> authors = publicationAuthorDAO.listByPublication(publication);
		for (PublicationAuthor author : authors) {
			publicationAuthorDAO.delete(author);
		}
		
		publicationDAO.delete(publication);
	}

	/* PublicationImages */

	public PublicationImage createPublicationImage(Publication publication, byte[] content, String contentType, User creator) {
		Date now = new Date();
		return publicationImageDAO.create(publication, content, contentType, now, now, creator, creator);
	}
	
	public PublicationImage findPublicationImageById(Long publicationImageId) {
		return publicationImageDAO.findById(publicationImageId);
	}

	public List<PublicationImage> listPublicationImagesByPublication(Publication publication) {
		return publicationImageDAO.listByPublication(publication);
	}

	public void deletePublicationImage(PublicationImage publicationImage) {
		publicationImageDAO.delete(publicationImage);
	}
	
	/* BookPublications */

	public BookPublication createBookPublication(User creator, String name, String description, Double price, PublicationImage defaultImage, Integer height, Integer width, Integer depth, Double weight, Integer numberOfPages, String license, List<GameLibraryTag> tags, Language language) {
		
		Date now = new Date();
		String urlName = createUrlName(name);

		BookPublication bookPublication = bookPublicationDAO.create(name, urlName, description, price, defaultImage, 
				now, creator, now, creator, Boolean.FALSE, height, width, depth, weight, numberOfPages, license, null, language, 0l, 0l);

		if (tags != null) {
		  for (GameLibraryTag tag : tags) {
			  publicationTagDAO.create(tag, bookPublication);
		  }
		}
		
		return bookPublication;
	}
	
	public BookPublication findBookPublicationById(Long id) {
		return bookPublicationDAO.findById(id);
	}
	
	public BookPublication updateNumberOfPages(BookPublication bookPublication, Integer numberOfPages) {
    return bookPublicationDAO.updateNumberOfPages(bookPublication, numberOfPages);
  }

  public BookPublication updateBookPublicationDownloadCount(BookPublication bookPublication, Long downloadCount) {
    return bookPublicationDAO.updateDownloadCount(bookPublication, downloadCount);
  }
  
  public BookPublication updateBookPublicationPrintCount(BookPublication bookPublication, Long printCount) {
    return bookPublicationDAO.updatePrintCount(bookPublication, printCount);
  }
  
  public BookPublication incBookPublicationDownloadCount(BookPublication bookPublication) {
    return updateBookPublicationDownloadCount(bookPublication, bookPublication.getDownloadCount() + 1);
  }

  public BookPublication incBookPublicationPrintCount(BookPublication bookPublication) {
    return updateBookPublicationPrintCount(bookPublication, bookPublication.getPrintCount() + 1);
  }

  public BookPublication deleteBookPublicationDownloableFile(BookPublication bookPublication) {
    PublicationFile downloadableFile = bookPublication.getDownloadableFile();
    if (downloadableFile != null) {
      bookPublicationDAO.updateDownlodableFile(bookPublication, null);
      deletePublicationFile(downloadableFile);
    }
    
    return bookPublication;
  }

  public BookPublication deleteBookPublicationPrintableFile(BookPublication bookPublication) {
    PublicationFile printableFile = bookPublication.getPrintableFile();
    if (printableFile != null) {
      bookPublicationDAO.updatePrintableFile(bookPublication, null);
      deletePublicationFile(printableFile);
    }
    
    return bookPublication;
  }

	/* PublicationFile */
	
	public PublicationFile createPublicationFile(byte[] content, String contentType) {
	  return publicationFileDAO.create(content, contentType);
	}
	
	public void deletePublicationFile(PublicationFile publicationFile) {
	  publicationFileDAO.delete(publicationFile);
	}
	
	public BookPublication setBookPublicationDownloadableFile(BookPublication bookPublication, byte[] content, String contentType, User creator) {
	  PublicationFile downloadableFile = bookPublication.getDownloadableFile();
	  if (downloadableFile == null) {
	    downloadableFile = createPublicationFile(content, contentType);
	    bookPublicationDAO.updateDownlodableFile(bookPublication, downloadableFile);
	  } else {
	    publicationFileDAO.updateContent(downloadableFile, content);
	    publicationFileDAO.updateContentType(downloadableFile, contentType);
	  }
	  
    return (BookPublication) updatedModified(bookPublication, creator, new Date());
	}
  
  public BookPublication setBookPublicationPrintableFile(BookPublication bookPublication, byte[] content, String contentType, User creator) {
    PublicationFile printableFile = bookPublication.getPrintableFile();
    if (printableFile == null) {
      printableFile = createPublicationFile(content, contentType);
      bookPublicationDAO.updatePrintableFile(bookPublication, printableFile);
    } else {
      publicationFileDAO.updateContent(printableFile, content);
      publicationFileDAO.updateContentType(printableFile, contentType);
    }
    
    return (BookPublication) updatedModified(bookPublication, creator, new Date());
  }
	
	/* PublicationAuthor */
	
	public PublicationAuthor createPublicationAuthor(Publication publication, User author) {
		return publicationAuthorDAO.create(publication, author);
	}
	
	public PublicationAuthor findPublicationAuthorByPublicationAndAuthor(Publication publication, User author) {
		return publicationAuthorDAO.findByPublicationAndAuthor(publication, author);
	}
	
	public List<PublicationAuthor> listPublicationAuthors(Publication publication) {
		return publicationAuthorDAO.listByPublication(publication);
	}
	
	public void deletePublicationAuthor(PublicationAuthor publicationAuthor) {
		publicationAuthorDAO.delete(publicationAuthor);
	}
	
	private String createUrlName(String name) {
	  return createUrlName(null, name);
	}
	
	private String createUrlName(Publication publication, String name) {
		int maxLength = 20;
		int padding = 0;
		do {
			String urlName = RequestUtils.createUrlName(name, maxLength);
			if (padding > 0) {
				urlName = urlName.concat(StringUtils.repeat('_', padding));
			}
			
			Publication existingPublication = publicationDAO.findByUrlName(urlName);
			if (existingPublication == null) {
				return urlName;
			}
			
			if (publication != null && existingPublication.getId().equals(existingPublication.getId())) {
			  return urlName;
			}
			
			if (maxLength < name.length()) {
				maxLength++;
			} else {
				padding++;
			}
		} while (true);
	}

}
