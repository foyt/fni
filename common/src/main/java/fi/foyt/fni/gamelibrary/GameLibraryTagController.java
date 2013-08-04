package fi.foyt.fni.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.gamelibrary.GameLibraryTagDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ProductTagDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Product;
import fi.foyt.fni.persistence.model.gamelibrary.ProductTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

@Stateful
@Dependent
public class GameLibraryTagController {

	@Inject
	private GameLibraryTagDAO gameLibraryTagDAO;
	
	@Inject
	private ProductTagDAO productTagDAO;
	
	/* Game Library Tags */

	public GameLibraryTag createTag(String text) {
		return gameLibraryTagDAO.create(text);
	}

	public GameLibraryTag findTagById(Long id) {
		return gameLibraryTagDAO.findById(id);
	}
	
	public GameLibraryTag findTagByText(String text) {
		return gameLibraryTagDAO.findByText(text);
	}
	
	public List<GameLibraryTag> listGameLibraryTags() {
		return gameLibraryTagDAO.listAll();
	}
	
	public List<GameLibraryTag> listActiveGameLibraryTags() {
		return productTagDAO.listGameLibraryTagsByProductPublished(Boolean.TRUE);
	}
	
	public List<GameLibraryTag> listProductGameLibraryTags(Product product) {
		List<GameLibraryTag> result = new ArrayList<GameLibraryTag>();
		
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
		GameLibraryTag gameLibraryTag = productTag.getTag();
		
		productTagDAO.delete(productTag);
		
		Long productCount = productTagDAO.countProductsByTag(gameLibraryTag);
		if (productCount == 0) {
			gameLibraryTagDAO.delete(gameLibraryTag);
		}
	}
}
