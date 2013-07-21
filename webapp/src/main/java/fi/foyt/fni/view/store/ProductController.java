package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.store.BookProductDAO;
import fi.foyt.fni.persistence.dao.store.FileProductDAO;
import fi.foyt.fni.persistence.dao.store.FileProductFileDAO;
import fi.foyt.fni.persistence.dao.store.ProductDAO;
import fi.foyt.fni.persistence.dao.store.ProductImageDAO;
import fi.foyt.fni.persistence.dao.store.ProductTagDAO;
import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.FileProduct;
import fi.foyt.fni.persistence.model.store.FileProductFile;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.StoreTag;
import fi.foyt.fni.persistence.model.users.User;

@Stateful
@Dependent
public class ProductController {
	
	@Inject
	private StoreTagController storeTagController;

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

	/* Products */

	public Product findProductById(Long id) {
		return productDAO.findById(id);
	}

	public List<Product> listAllProducts() {
		return productDAO.listAll();
	}

	public List<Product> listProductsByTags(String... tags) {
		List<StoreTag> storeTags = new ArrayList<>();

		for (String tag : tags) {
			storeTags.add(storeTagController.findTagByText(tag));
		}

		return listProductsByTags(storeTags);
	}

	public List<Product> listProductsByTags(List<StoreTag> storeTags) {
		return productTagDAO.listProductsByStoreTags(storeTags);
	}

	public List<Product> listRecentProducts(int maxRecentProduct) {
		return productDAO.listByPublishedOrderByCreated(Boolean.TRUE, 0, maxRecentProduct);
	}

	public List<Product> listUnpublishedProducts() {
		return productDAO.listByPublished(Boolean.FALSE);
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
		
		for (ProductTag productTag : storeTagController.listProductTags(product)) {
			storeTagController.deleteProductTag(productTag);
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

	public BookProduct createBookProduct(User creator, String name, String description, Boolean requiresDelivery, Boolean downloadable, Boolean purchasable, Double price, ProductImage defaultImage, Integer height, Integer width, Integer depth, Double weight, String author, Integer numberOfPages, List<StoreTag> tags) {
		
		Date now = new Date();
		
		BookProduct bookProduct = bookProductDAO.create(name, description, price, downloadable, purchasable, defaultImage, now, creator, now, creator, Boolean.FALSE, requiresDelivery, height, width
				, depth, weight, author, numberOfPages);

		for (StoreTag tag : tags) {
			productTagDAO.create(tag, bookProduct);
		}
		
		return bookProduct;
	}
	
	public BookProduct findBookProductById(Long id) {
		return bookProductDAO.findById(id);
	}
	
	public BookProduct updateBookProduct(fi.foyt.fni.persistence.model.store.BookProduct bookProduct, Double price, String name,
			String description, List<StoreTag> tags, Boolean published, Boolean requiresDelivery, Boolean downloadable, 
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
		
		List<StoreTag> addTags = new ArrayList<>(tags);
		
		Map<Long, ProductTag> existingTagMap = new HashMap<Long, ProductTag>();
		List<ProductTag> existingTags = storeTagController.listProductTags(bookProduct);
		for (ProductTag existingTag : existingTags) {
			existingTagMap.put(existingTag.getTag().getId(), existingTag);
		}
		
		for (int i = addTags.size() - 1; i >= 0; i--) {
			StoreTag addTag = addTags.get(i);
			
			if (existingTagMap.containsKey(addTag.getId())) {
				addTags.remove(i);
			} 
			
			existingTagMap.remove(addTag.getId());
		}
		
		for (ProductTag removeTag : existingTagMap.values()) {
			storeTagController.deleteProductTag(removeTag);
		}
		
		for (StoreTag storeTag : addTags) {
			productTagDAO.create(storeTag, bookProduct);
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
}
