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
import fi.foyt.fni.persistence.model.gamelibrary.Product;
import fi.foyt.fni.persistence.model.gamelibrary.ProductImage;
import fi.foyt.fni.persistence.model.gamelibrary.ProductTag;
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

	public Product findProductById(Long id) {
		return productDAO.findById(id);
	}

	public Product findProductByUrlName(String urlName) {
		return productDAO.findByUrlName(urlName);
	}

	public List<Product> listAllProducts() {
		return productDAO.listAll();
	}

	public List<Product> listProductsByTags(String... tags) {
		List<GameLibraryTag> gameLibraryTags = new ArrayList<>();

		for (String tag : tags) {
			gameLibraryTags.add(gameLibraryTagController.findTagByText(tag));
		}

		return listProductsByTags(gameLibraryTags);
	}

	public List<Product> listProductsByTags(List<GameLibraryTag> gameLibraryTags) {
		return productTagDAO.listProductsByGameLibraryTags(gameLibraryTags);
	}

	public List<Product> listRecentProducts(int maxRecentProduct) {
		return productDAO.listByPublishedOrderByCreated(Boolean.TRUE, 0, maxRecentProduct);
	}

	public List<Product> listUnpublishedProducts() {
		return productDAO.listByPublished(Boolean.FALSE);
	}
	
	public List<Product> listPublishedProductsByCreator(User creator) {
		return productDAO.listByCreatorAndPublished(creator, Boolean.TRUE);
	}

	public Product updatedModified(Product product, User modifier, Date modified) {
		productDAO.updateModified(product, modified);
		productDAO.updateModifier(product, modifier);
		
		return product;
	}
	
	public Product publishProduct(Product product) {
		return productDAO.updatePublished(product, Boolean.TRUE);
	}

	public Product unpublishProduct(Product product) {
		return productDAO.updatePublished(product, Boolean.FALSE);
	}
	
	public Product updateProductDefaultImage(Product product, ProductImage productImage) {
		return productDAO.updateDefaultImage(product, productImage);
	}
	
	public void deleteProduct(Product product) {
		for (ProductImage productImage : listProductImageByProduct(product)) {
			deleteProductImage(productImage);
		}
		
		if (product instanceof FileProduct) {
			FileProductFile file = ((FileProduct) product).getFile();
			if (file != null) {
			  deleteFileProductFile(file);
			}
		}
		
		for (ProductTag productTag : gameLibraryTagController.listProductTags(product)) {
			gameLibraryTagController.deleteProductTag(productTag);
		}
		
		productDAO.delete(product);
	}

	/* ProductImages */

	public ProductImage createProductImage(Product product, byte[] content, String contentType, User creator) {
		Date now = new Date();
		return productImageDAO.create(product, content, contentType, now, now, creator, creator);
	}
	
	public ProductImage findProductImageById(Long productImageId) {
		return productImageDAO.findById(productImageId);
	}

	public List<ProductImage> listProductImageByProduct(Product product) {
		return productImageDAO.listByProduct(product);
	}

	public void deleteProductImage(ProductImage productImage) {
		productImageDAO.delete(productImage);
	}
	/* BookProducts */

	public BookProduct createBookProduct(User creator, String name, String description, Boolean requiresDelivery, Boolean downloadable, Boolean purchasable, Double price, ProductImage defaultImage, Integer height, Integer width, Integer depth, Double weight, String author, Integer numberOfPages, List<GameLibraryTag> tags) {
		
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
		
		Map<Long, ProductTag> existingTagMap = new HashMap<Long, ProductTag>();
		List<ProductTag> existingTags = gameLibraryTagController.listProductTags(bookProduct);
		for (ProductTag existingTag : existingTags) {
			existingTagMap.put(existingTag.getTag().getId(), existingTag);
		}
		
		for (int i = addTags.size() - 1; i >= 0; i--) {
			GameLibraryTag addTag = addTags.get(i);
			
			if (existingTagMap.containsKey(addTag.getId())) {
				addTags.remove(i);
			} 
			
			existingTagMap.remove(addTag.getId());
		}
		
		for (ProductTag removeTag : existingTagMap.values()) {
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
			
			Product product = productDAO.findByUrlName(urlName);
			if (product == null) {
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
