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

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.dao.gamelibrary.BookProductDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.FileProductDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.FileProductFileDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ProductDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ProductImageDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ProductTagDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookProduct;
import fi.foyt.fni.persistence.model.gamelibrary.FileProduct;
import fi.foyt.fni.persistence.model.gamelibrary.FileProductFile;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.servlet.RequestUtils;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;

@Stateful
@Dependent
public class ProductController {
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ProductDAO productDAO;

	@Inject
	private ProductTagDAO productTagDAO;

	@Inject
	private ProductImageDAO productImageDAO;

	@Inject
	private BookProductDAO bookProductDAO;

	@Inject
	private FileProductDAO fileProductDAO;

	@Inject
	private FileProductFileDAO fileProductFileDAO;

	@Inject
	private ForumController forumController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	/* Products */

	public Publication findProductById(Long id) {
		return productDAO.findById(id);
	}

	public Publication findProductByUrlName(String urlName) {
		return productDAO.findByUrlName(urlName);
	}

	public List<Publication> listAllProducts() {
		return productDAO.listAll();
	}

	public List<Publication> listProductsByTags(String... tags) {
		List<GameLibraryTag> gameLibraryTags = new ArrayList<>();

		for (String tag : tags) {
			gameLibraryTags.add(gameLibraryTagController.findTagByText(tag));
		}

		return listProductsByTags(gameLibraryTags);
	}

	public List<Publication> listProductsByTags(List<GameLibraryTag> gameLibraryTags) {
		return productTagDAO.listProductsByGameLibraryTags(gameLibraryTags);
	}

	public List<Publication> listRecentProducts(int maxRecentProduct) {
		return productDAO.listByPublishedOrderByCreated(Boolean.TRUE, 0, maxRecentProduct);
	}

	public List<Publication> listUnpublishedProducts() {
		return productDAO.listByPublished(Boolean.FALSE);
	}
	
	public List<Publication> listPublishedProductsByCreator(User creator) {
		return productDAO.listByCreatorAndPublished(creator, Boolean.TRUE);
	}

	public Publication updatedModified(Publication publication, User modifier, Date modified) {
		productDAO.updateModified(publication, modified);
		productDAO.updateModifier(publication, modifier);
		
		return publication;
	}
	
	public Publication publishProduct(Publication publication) {
		return productDAO.updatePublished(publication, Boolean.TRUE);
	}

	public Publication unpublishProduct(Publication publication) {
		return productDAO.updatePublished(publication, Boolean.FALSE);
	}
	
	public Publication updateProductDefaultImage(Publication publication, PublicationImage publicationImage) {
		return productDAO.updateDefaultImage(publication, publicationImage);
	}
	
	public void deleteProduct(Publication publication) {
		for (PublicationImage publicationImage : listProductImageByProduct(publication)) {
			deleteProductImage(publicationImage);
		}
		
		if (publication instanceof FileProduct) {
			FileProductFile file = ((FileProduct) publication).getFile();
			if (file != null) {
			  deleteFileProductFile(file);
			}
		}
		
		for (PublicationTag publicationTag : gameLibraryTagController.listProductTags(publication)) {
			gameLibraryTagController.deleteProductTag(publicationTag);
		}
		
		productDAO.delete(publication);
	}

	/* ProductImages */

	public PublicationImage createProductImage(Publication publication, byte[] content, String contentType, User creator) {
		Date now = new Date();
		return productImageDAO.create(publication, content, contentType, now, now, creator, creator);
	}
	
	public PublicationImage findProductImageById(Long productImageId) {
		return productImageDAO.findById(productImageId);
	}

	public List<PublicationImage> listProductImageByProduct(Publication publication) {
		return productImageDAO.listByProduct(publication);
	}

	public void deleteProductImage(PublicationImage publicationImage) {
		productImageDAO.delete(publicationImage);
	}
	/* BookProducts */

	public BookProduct createBookProduct(User creator, String name, String description, Boolean requiresDelivery, Boolean downloadable, Boolean purchasable, Double price, PublicationImage defaultImage, Integer height, Integer width, Integer depth, Double weight, String author, Integer numberOfPages, List<GameLibraryTag> tags) {
		
		Date now = new Date();
		Long forumId = systemSettingsController.getGameLibraryPublicationForumId();
		Forum forum = forumController.findForumById(forumId);
		ForumTopic forumTopic = forumController.createTopic(forum, name, creator);
		String urlName = createUrlName(name);

		BookProduct bookProduct = bookProductDAO.create(name, urlName, description, price, downloadable, purchasable, defaultImage, 
				now, creator, now, creator, Boolean.FALSE, requiresDelivery, height, width, depth, weight, author, numberOfPages, forumTopic);

		for (GameLibraryTag tag : tags) {
			productTagDAO.create(tag, bookProduct);
		}
		
		return bookProduct;
	}
	
	public BookProduct findBookProductById(Long id) {
		return bookProductDAO.findById(id);
	}
	
	public BookProduct updateBookProduct(fi.foyt.fni.persistence.model.gamelibrary.BookProduct bookProduct, Double price, String name,
			String description, List<GameLibraryTag> tags, Boolean published, Boolean requiresDelivery, Boolean downloadable, 
			Boolean purchasable, Double weight, Integer width, Integer height, Integer depth, Integer numberOfPages, String author, 
			User modifier) {

		productDAO.updateName(bookProduct, name);
		productDAO.updateDescription(bookProduct, description);
		productDAO.updatePurchasable(bookProduct, purchasable);
		productDAO.updateWeight(bookProduct, weight);
		productDAO.updateWidth(bookProduct, width);
		productDAO.updateHeight(bookProduct, height);
		productDAO.updateDepth(bookProduct, depth);
		bookProductDAO.updateNumberOfPages(bookProduct, numberOfPages);
		bookProductDAO.updateAuthor(bookProduct, author);
		
		List<GameLibraryTag> addTags = new ArrayList<>(tags);
		
		Map<Long, PublicationTag> existingTagMap = new HashMap<Long, PublicationTag>();
		List<PublicationTag> existingTags = gameLibraryTagController.listProductTags(bookProduct);
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
			gameLibraryTagController.deleteProductTag(removeTag);
		}
		
		for (GameLibraryTag gameLibraryTag : addTags) {
			productTagDAO.create(gameLibraryTag, bookProduct);
		}
		
		productDAO.updatePrice(bookProduct, price);
		productDAO.updatePublished(bookProduct, published);
		productDAO.updateRequiresDelivery(bookProduct, requiresDelivery);
		bookProductDAO.updateDownloadable(bookProduct, downloadable);
		
		updatedModified(bookProduct, modifier, new Date());
		
		return bookProduct;
	}

	/* FileProducts */

	public FileProduct findFileProductById(Long id) {
		return fileProductDAO.findById(id);
	}
	
	public FileProduct updateFileProductFile(FileProduct fileProduct, FileProductFile file) {
		return fileProductDAO.updateFile(fileProduct, file);
	}

	/* FileProductFiles */

	public FileProductFile createFileProductFile(FileProduct fileProduct, String contentType, byte[] content, User creator) {
	  // TODO: Should not be needed but ProductFileServlet crashes without this...
		fileProduct = fileProductDAO.findById(fileProduct.getId());
		FileProductFile file = fileProductFileDAO.create(content, contentType);
		updatedModified(fileProduct, creator, new Date());
		fileProductDAO.updateFile(fileProduct, file);
		return file;
	}
	
	public FileProductFile updateFileProductFile(FileProduct fileProduct, String contentType, byte[] content, User modifier) {
	  // TODO: Should not be needed but ProductFileServlet crashes without this...
		fileProduct = fileProductDAO.findById(fileProduct.getId());
		FileProductFile file = fileProductFileDAO.updateContent(fileProductFileDAO.updateContentType(fileProduct.getFile(), contentType), content);
		updatedModified(fileProduct, modifier, new Date());
		fileProductDAO.updateFile(fileProduct, file);
		return file;
	}
	
	public void deleteFileProductFile(FileProductFile fileProductFile) {
		fileProductFileDAO.delete(fileProductFile);
	}
	
	private String createUrlName(String name) {
		int maxLength = 20;
		int padding = 0;
		do {
			String urlName = RequestUtils.createUrlName(name, maxLength);
			if (padding > 0) {
				urlName = urlName.concat(StringUtils.repeat('_', padding));
			}
			
			Publication publication = productDAO.findByUrlName(urlName);
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
