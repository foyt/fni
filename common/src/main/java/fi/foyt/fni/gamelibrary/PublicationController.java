package fi.foyt.fni.gamelibrary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
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
import fi.foyt.fni.persistence.dao.gamelibrary.BookPublicationFileDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationAuthorDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationImageDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.PublicationTagDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
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
import fi.foyt.fni.utils.faces.FacesUtils;
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
	private BookPublicationFileDAO bookPublicationFileDAO;

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

	public List<Publication> listUnpublishedPublications(User creator) {
		return publicationDAO.listByCreatorAndPublished(creator, Boolean.FALSE);
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
		queryStringBuilder.append(")");
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser parser = new QueryParser(Version.LUCENE_35, "descriptionPlain", analyzer);
		Query luceneQuery = parser.parse(queryStringBuilder.toString());
    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, BookPublication.class);
		@SuppressWarnings("unchecked")
		List<Publication> searchResults = query.getResultList();
    for (Publication searchResult : searchResults) {
    	String link = new StringBuilder()
    	  .append("gamelibrary/")
    	  .append(searchResult.getUrlName())
    	  .toString();
    	result.add(new SearchResult<Publication>(searchResult, searchResult.getName(), link, null, null));
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
	
	public Publication publishPublication(Publication publication) {
		return publicationDAO.updatePublished(publication, Boolean.TRUE);
	}

	public Publication unpublishPublication(Publication publication) {
		return publicationDAO.updatePublished(publication, Boolean.FALSE);
	}
	
	public Publication updatePublicationDefaultImage(Publication publication, PublicationImage publicationImage) {
		return publicationDAO.updateDefaultImage(publication, publicationImage);
	}
	
	public void deletePublication(Publication publication) {
		for (PublicationImage publicationImage : listPublicationImagesByPublication(publication)) {
			deletePublicationImage(publicationImage);
		}
		
		if (publication instanceof BookPublication) {
			PublicationFile file = ((BookPublication) publication).getFile();
			if (file != null) {
				updateBookPublicationFile((BookPublication) publication, null);
			  deleteFilePublicationFile(file);
			}
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

	public BookPublication createBookPublication(User creator, String name, String description, Boolean requiresDelivery, Boolean downloadable, Boolean purchasable, Double price, PublicationImage defaultImage, Integer height, Integer width, Integer depth, Double weight, Integer numberOfPages, String license, List<GameLibraryTag> tags) {
		
		Date now = new Date();
		User systemUser = systemSettingsController.getSystemUser();
		
		Long forumId = systemSettingsController.getGameLibraryPublicationForumId();
		Forum forum = forumController.findForumById(forumId);
		ForumTopic forumTopic = forumController.createTopic(forum, name, systemUser);
		String urlName = createUrlName(name);

		BookPublication bookPublication = bookPublicationDAO.create(name, urlName, description, price, downloadable, purchasable, defaultImage, 
				now, creator, now, creator, Boolean.FALSE, requiresDelivery, height, width, depth, weight, numberOfPages, license, forumTopic);

		for (GameLibraryTag tag : tags) {
			publicationTagDAO.create(tag, bookPublication);
		}
		
		String publicationUrl = new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/")
  	  .append(bookPublication.getUrlName())
  	  .toString();
		
		String initialForumPostContent = FacesUtils.getLocalizedValue("gamelibrary.bookPublication.initialForumMessage", publicationUrl, bookPublication.getName());
		forumController.createForumPost(forumTopic, systemUser, initialForumPostContent);

		return bookPublication;
	}
	
	public BookPublication findBookPublicationById(Long id) {
		return bookPublicationDAO.findById(id);
	}
	
	public BookPublication updateBookPublication(fi.foyt.fni.persistence.model.gamelibrary.BookPublication bookPublication, Double price, String name,
			String description, List<GameLibraryTag> tags, Boolean published, Boolean requiresDelivery, Boolean downloadable, 
			Boolean purchasable, Double weight, Integer width, Integer height, Integer depth, Integer numberOfPages, User modifier) {

		publicationDAO.updateName(bookPublication, name);
		publicationDAO.updateDescription(bookPublication, description);
		publicationDAO.updatePurchasable(bookPublication, purchasable);
		publicationDAO.updateWeight(bookPublication, weight);
		publicationDAO.updateWidth(bookPublication, width);
		publicationDAO.updateHeight(bookPublication, height);
		publicationDAO.updateDepth(bookPublication, depth);
		bookPublicationDAO.updateNumberOfPages(bookPublication, numberOfPages);
		
		List<GameLibraryTag> addTags = new ArrayList<>(tags);
		
		Map<Long, PublicationTag> existingTagMap = new HashMap<Long, PublicationTag>();
		List<PublicationTag> existingTags = gameLibraryTagController.listPublicationTags(bookPublication);
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
			publicationTagDAO.create(gameLibraryTag, bookPublication);
		}
		
		publicationDAO.updatePrice(bookPublication, price);
		publicationDAO.updatePublished(bookPublication, published);
		publicationDAO.updateRequiresDelivery(bookPublication, requiresDelivery);
		bookPublicationDAO.updateDownloadable(bookPublication, downloadable);
		
		updatedModified(bookPublication, modifier, new Date());
		
		return bookPublication;
	}

	public BookPublication updateBookPublicationFile(BookPublication bookPublication, PublicationFile file) {
		return bookPublicationDAO.updateFile(bookPublication, file);
	}

	/* PublicationFile */

	public PublicationFile createBookPublicationFile(BookPublication bookPublication, String contentType, byte[] content, User creator) {
		PublicationFile file = bookPublicationFileDAO.create(content, contentType);
		updatedModified(bookPublication, creator, new Date());
		bookPublicationDAO.updateFile(bookPublication, file);
		return file;
	}
	
	public PublicationFile updateBookPublicationFile(BookPublication bookPublication, String contentType, byte[] content, User modifier) {
		PublicationFile file = bookPublicationFileDAO.updateContent(bookPublicationFileDAO.updateContentType(bookPublication.getFile(), contentType), content);
		updatedModified(bookPublication, modifier, new Date());
		bookPublicationDAO.updateFile(bookPublication, file);
		return file;
	}
	
	public void deleteFilePublicationFile(PublicationFile publicationFile) {
		bookPublicationFileDAO.delete(publicationFile);
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
		int maxLength = 20;
		int padding = 0;
		do {
			String urlName = RequestUtils.createUrlName(name, maxLength);
			if (padding > 0) {
				urlName = urlName.concat(StringUtils.repeat('_', padding));
			}
			
			Publication publication = publicationDAO.findByUrlName(urlName);
			if (publication == null) {
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
