package fi.foyt.fni.persistence.dao.gamelibrary;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag_;

@DAO
public class GameLibraryTagDAO extends GenericDAO<GameLibraryTag> {
  
	private static final long serialVersionUID = 1L;

	public GameLibraryTag create(String text) {
		GameLibraryTag gameLibraryTag = new GameLibraryTag();
		gameLibraryTag.setText(text);
		getEntityManager().persist(gameLibraryTag);
		return gameLibraryTag;
	}

	public GameLibraryTag findByText(String text) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GameLibraryTag> criteria = criteriaBuilder.createQuery(GameLibraryTag.class);
    Root<GameLibraryTag> root = criteria.from(GameLibraryTag.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(GameLibraryTag_.text), text)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}
	
}
