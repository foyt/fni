package fi.foyt.fni.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.gamelibrary.GameLibraryTagDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ProductTagDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
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
	
	public List<GameLibraryTag> listProductGameLibraryTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<GameLibraryTag>();
		
		List<PublicationTag> publicationTags = productTagDAO.listByProduct(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}
		
		return result;
	}
	
	/* Product Tags */
	
	public List<PublicationTag> listProductTags(Publication publication) {
		return productTagDAO.listByProduct(publication);
	}

	public void deleteProductTag(PublicationTag publicationTag) {
		GameLibraryTag gameLibraryTag = publicationTag.getTag();
		
		productTagDAO.delete(publicationTag);
		
		Long productCount = productTagDAO.countProductsByTag(gameLibraryTag);
		if (productCount == 0) {
			gameLibraryTagDAO.delete(gameLibraryTag);
		}
	}
}
