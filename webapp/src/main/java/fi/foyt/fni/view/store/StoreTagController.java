package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.store.ProductTagDAO;
import fi.foyt.fni.persistence.dao.store.StoreTagDAO;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.StoreTag;

@Stateful
@Dependent
public class StoreTagController {

	@Inject
	private StoreTagDAO storeTagDAO;
	
	@Inject
	private ProductTagDAO productTagDAO;
	
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
	
	public List<StoreTag> listStoreTags() {
		return storeTagDAO.listAll();
	}
	
	public List<StoreTag> listActiveStoreTags() {
		return productTagDAO.listStoreTagsByProductPublished(Boolean.TRUE);
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
	
	public List<ProductTag> listProductTags(Product product) {
		return productTagDAO.listByProduct(product);
	}

	public void deleteProductTag(ProductTag productTag) {
		StoreTag storeTag = productTag.getTag();
		
		productTagDAO.delete(productTag);
		
		Long productCount = productTagDAO.countProductsByTag(storeTag);
		if (productCount == 0) {
			storeTagDAO.delete(storeTag);
		}
	}
}
