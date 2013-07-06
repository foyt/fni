package fi.foyt.fni.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LocalizedStringDAO;
import fi.foyt.fni.persistence.dao.common.MultilingualStringDAO;
import fi.foyt.fni.persistence.dao.store.BookProductDAO;
import fi.foyt.fni.persistence.dao.store.FileProductDAO;
import fi.foyt.fni.persistence.dao.store.FileProductFileDAO;
import fi.foyt.fni.persistence.dao.store.ProductDAO;
import fi.foyt.fni.persistence.dao.store.ProductDetailDAO;
import fi.foyt.fni.persistence.dao.store.ProductImageDAO;
import fi.foyt.fni.persistence.dao.store.ProductTagDAO;
import fi.foyt.fni.persistence.dao.store.StoreDetailDAO;
import fi.foyt.fni.persistence.dao.store.StoreTagDAO;
import fi.foyt.fni.persistence.model.common.LocalizedString;
import fi.foyt.fni.persistence.model.common.MultilingualString;
import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.FileProduct;
import fi.foyt.fni.persistence.model.store.FileProductFile;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductDetail;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.StoreDetail;
import fi.foyt.fni.persistence.model.store.StoreTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Stateful
public class StoreController {
	
	private static final int RECENT_PRODUCT_COUNT = 5;
	
	@Inject
	private SystemSettingsController systemSettingsController;
  
	@Inject
	@DAO
	private StoreTagDAO storeTagDAO;

	@Inject
	@DAO
	private ProductTagDAO productTagDAO;
	
	@Inject
	@DAO
	private ProductDAO productDAO;
	
	@Inject
	@DAO
	private ProductImageDAO productImageDAO;
	
	@Inject
	@DAO
	private MultilingualStringDAO multilingualStringDAO;
	
	@Inject
	@DAO
	private LocalizedStringDAO localizedStringDAO;
	
	@Inject
	@DAO
	private BookProductDAO bookProductDAO; 

	@Inject
	@DAO
	private FileProductDAO fileProductDAO;
	
	@Inject
	@DAO
	private FileProductFileDAO fileProductFileDAO;

	@Inject
	@DAO
	private StoreDetailDAO storeDetailDAO;
	
	@Inject
	@DAO
	private ProductDetailDAO productDetailDAO;
	
	/* Store Tags */

	public StoreTag createTag(String text) {
		return storeTagDAO.create(text);
	}

	public StoreTag findTagById(Long id) {
		return storeTagDAO.findById(id);
	}
	
	public StoreTag findTagByText(String text) {
		return storeTagDAO.findByText(text);
	}

	public List<StoreTag> listTags() {
		return storeTagDAO.listAll();
	}

	public List<StoreTag> listProductStoreTags(Product product) {
		List<StoreTag> result = new ArrayList<StoreTag>();
		
		List<ProductTag> productTags = productTagDAO.listByProduct(product);
		for (ProductTag productTag : productTags) {
			result.add(productTag.getTag());
		}
		
		return result;
	}
	
	/* Product Tags */

	private List<ProductTag> listProductTagsByProduct(Product product) {
		return productTagDAO.listByProduct(product);
	}

	public List<StoreTag> listStoreTagsByProduct(Product product) {
		List<StoreTag> result = new ArrayList<>();
		
		List<ProductTag> productTags = productTagDAO.listByProduct(product);
		for (ProductTag productTag : productTags) {
			result.add(productTag.getTag());
		}
		
		return result;
	}

	public void deleteProductTag(ProductTag productTag) {
		StoreTag storeTag = productTag.getTag();
		
		productTagDAO.delete(productTag);
		
		Long productCount = productTagDAO.countProductsByTag(storeTag);
		if (productCount == 0) {
			storeTagDAO.delete(storeTag);
		}
	}
	
	/* Store Details */

	public StoreDetail createStoreDetail(String name) {
		return storeDetailDAO.create(name);
	}

	public StoreDetail findStoreDetailByName(String name) {
		return storeDetailDAO.findByName(name);
	}

	public List<StoreDetail> listStoreDetails() {
		return storeDetailDAO.listAll();
	}

	/* Product Details */

	public ProductDetail createProductDetail(Product product, StoreDetail storeDetail, String value) {
		return productDetailDAO.create(storeDetail, product, value);
	}
	
	public ProductDetail findProductDetail(Product product, StoreDetail storeDetail) {
		return productDetailDAO.findByProductAndDetail(product, storeDetail);
	}

	public List<ProductDetail> listProductDetailsByProduct(Product product) {
		return productDetailDAO.listByProduct(product);
	}

	private void updateProductDetailValue(ProductDetail productDetail, String value) {
		productDetailDAO.updateValue(productDetail, value);
	}

	public void setProductDetail(Product product, String name, String value) {
		StoreDetail storeDetail = findStoreDetailByName(name);
		if (storeDetail == null) {
			storeDetail = createStoreDetail(name);
		}
		
		ProductDetail productDetail = findProductDetail(product, storeDetail);
		if (productDetail == null) {
			if (StringUtils.isNotBlank(value)) {
			  createProductDetail(product, storeDetail, value);
			}
		} else {
			if (StringUtils.isNotBlank(value)) {
				updateProductDetailValue(productDetail, value);
			} else {
				deleteProductDetailValue(productDetail);
			}
		}
	}

	public Map<String, String> getProductDetailMap(Product product) {
		Map<String, String> result = new HashMap<String, String>();
		
		List<ProductDetail> productDetails = listProductDetailsByProduct(product);
		for (ProductDetail productDetail : productDetails) {
			String name = productDetail.getDetail().getName();
			String value = productDetail.getValue();
			result.put(name, value);
		}
		
		return result;
	}

	public void deleteProductDetailValue(ProductDetail productDetail) {
		productDetailDAO.delete(productDetail);
	}

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
		  storeTags.add(findTagByText(tag));
		}
		
		return listProductsByTags(storeTags);
	}
	
	public List<Product> listProductsByTags(List<StoreTag> storeTags) {
		return productTagDAO.listProductsByStoreTags(storeTags);
	}
	
	public List<Product> listRecentProducts() {
		return productDAO.listAllOrderByCreated(0, RECENT_PRODUCT_COUNT);
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
		
		for (ProductDetail productDetail : listProductDetailsByProduct(product)) {
			deleteProductDetailValue(productDetail);
		}
		
		for (ProductTag productTag : listProductTagsByProduct(product)) {
			deleteProductTag(productTag);
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

	public BookProduct createBookProduct(User creator, Map<Locale, String> names, Map<Locale, String> descriptions, Boolean downloadable, Double price, ProductImage defaultImage, List<StoreTag> tags, Map<String, String> details) {
		MultilingualString name = multilingualStringDAO.create();
		MultilingualString description = multilingualStringDAO.create();
		
		for (Locale locale : names.keySet()) {
			String value = names.get(locale);
			localizedStringDAO.create(name, locale, value);
		}
		
		for (Locale locale : descriptions.keySet()) {
			String value = descriptions.get(locale);
			localizedStringDAO.create(description, locale, value);
		}
		
		Date now = new Date();
		
		BookProduct bookProduct = bookProductDAO.create(name, description, price, downloadable, defaultImage, now, creator, now, creator, Boolean.FALSE);

		for (StoreTag tag : tags) {
			productTagDAO.create(tag, bookProduct);
		}
		
		return bookProduct;
	}
	
	public BookProduct findBookProductById(Long id) {
		return bookProductDAO.findById(id);
	}
	
	public BookProduct updateBookProduct(fi.foyt.fni.persistence.model.store.BookProduct bookProduct, Double price, Map<Locale, String> names,
			Map<Locale, String> descriptions, Map<String, String> details, List<String> tags, Boolean published, Boolean downloadable, User modifier) {
		
		for (Locale locale : names.keySet()) {
			setMultiLingualString(bookProduct.getName(), locale, names.get(locale));
		}
		
		for (Locale locale : descriptions.keySet()) {
			setMultiLingualString(bookProduct.getDescription(), locale, names.get(locale));
		}
		
		if (details == null) {
			details = new HashMap<String, String>();
		}
		
		Map<String, String> existingDetails = getProductDetailMap(bookProduct);
		
		for (String name : details.keySet()) {
			String value = details.get(name);
			existingDetails.remove(name);
			setProductDetail(bookProduct, name, value);
		}
		
		for (String name : existingDetails.keySet()) {
			setProductDetail(bookProduct, name, null);
		}
		
		List<StoreTag> addTags = new ArrayList<>();
		
		for (String tag : tags) {
			StoreTag storeTag = findTagByText(tag);
			if (storeTag == null) {
				storeTag = createTag(tag);
			}
			
		  addTags.add(storeTag);
		}
		
		Map<Long, ProductTag> existingTagMap = new HashMap<Long, ProductTag>();
		List<ProductTag> existingTags = listProductTagsByProduct(bookProduct);
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
			deleteProductTag(removeTag);
		}
		
		for (StoreTag storeTag : addTags) {
			productTagDAO.create(storeTag, bookProduct);
		}
		
		productDAO.updateModified(bookProduct, new Date());
		productDAO.updateModifier(bookProduct, modifier);
		productDAO.updatePrice(bookProduct, price);
		productDAO.updatePublished(bookProduct, published);
		bookProductDAO.updateDownloadable(bookProduct, downloadable);
		
		return bookProduct;
	}

	/* FileProducts */

	private void setMultiLingualString(MultilingualString string, Locale locale, String value) {
		LocalizedString localizedString = localizedStringDAO.findByMultilingualStringAndLocale(string, locale);
		
		if (localizedString == null) {
			localizedStringDAO.create(string, locale, value);
		} else {
			localizedStringDAO.updateValue(localizedString, value);
		}
	}

	public FileProduct findFileProductById(Long id) {
		return fileProductDAO.findById(id);
	}

	public FileProduct updateFileProductFile(fi.foyt.fni.persistence.model.store.FileProduct fileProduct, FileProductFile file) {
		return fileProductDAO.updateFile(fileProduct, file);
	}
	
	/* FileProductFiles */

	public FileProductFile createFileProductFile(byte[] content, String contentType) {
		return fileProductFileDAO.create(content, contentType);
	}
	
	public void deleteFileProductFile(FileProductFile fileProductFile) {
		fileProductFileDAO.delete(fileProductFile);
	}
	
}
