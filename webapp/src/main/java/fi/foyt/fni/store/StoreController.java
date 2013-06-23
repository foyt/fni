package fi.foyt.fni.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LocalizedStringDAO;
import fi.foyt.fni.persistence.dao.common.MultilingualStringDAO;
import fi.foyt.fni.persistence.dao.store.ProductDAO;
import fi.foyt.fni.persistence.dao.store.ProductImageDAO;
import fi.foyt.fni.persistence.dao.store.ProductTagDAO;
import fi.foyt.fni.persistence.dao.store.StoreTagDAO;
import fi.foyt.fni.persistence.model.common.LocalizedString;
import fi.foyt.fni.persistence.model.common.MultilingualString;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.StoreTag;
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
	
	/* Tags */

	public StoreTag createTag(Map<Locale, String> texts) {
		MultilingualString text = multilingualStringDAO.create();
		Map<Locale, LocalizedString> localeStrings = new HashMap<>();
		
		for (Locale locale : texts.keySet()) {
			String localeText = texts.get(locale);
			LocalizedString localizedString = localizedStringDAO.create(text, locale, localeText);
			localeStrings.put(locale, localizedString);
		}
		
		LocalizedString defaultString = null;
		
		Locale defaultLocale = systemSettingsController.getLocaleSetting("system.defaultLocale");
		if (localeStrings.containsKey(defaultLocale)) {
			defaultString = localeStrings.get(defaultLocale);
		} else {
			Locale defaultLocale2 = new Locale(defaultLocale.getLanguage());
			if (localeStrings.containsKey(defaultLocale2)) {
				defaultString = localeStrings.get(defaultLocale2);
			}
		}
		
		if (defaultString != null) {
		  multilingualStringDAO.updateDefaultString(text, defaultString);
		}
		
		return storeTagDAO.create(text);
	}

	public StoreTag findStoreTagById(Long id) {
		return storeTagDAO.findById(id);
	}

	public List<StoreTag> listStoreTags() {
		return storeTagDAO.listAll();
	}
	
	public List<StoreTag> listProductTags(Product product) {
		List<StoreTag> result = new ArrayList<>();
		
		List<ProductTag> productTags = productTagDAO.listByProduct(product);
		for (ProductTag productTag : productTags) {
			result.add(productTag.getTag());
		}
		
		return result;
	}
	
	/* Products */
	
	public List<Product> listRecentProducts() {
		return productDAO.listAllOrderByCreated(0, RECENT_PRODUCT_COUNT);
	}

	public ProductImage findProductImageById(Long productImageId) {
		return productImageDAO.findById(productImageId);
	}

}
