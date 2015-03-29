package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRoll;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRoll_;
import fi.foyt.fni.persistence.model.users.User;

public class CharacterSheetRollDAO extends GenericDAO<CharacterSheetRoll> {

	private static final long serialVersionUID = 1L;

	public CharacterSheetRoll create(CharacterSheet sheet, User user, String label, String roll, Integer result) {
    CharacterSheetRoll characterSheetData = new CharacterSheetRoll();

    characterSheetData.setUser(user);
    characterSheetData.setLabel(label);
    characterSheetData.setResult(result);
    characterSheetData.setRoll(roll);
    characterSheetData.setSheet(sheet);

    return persist(characterSheetData);
  }
	
  public List<CharacterSheetRoll> listBySheet(CharacterSheet sheet) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetRoll> criteria = criteriaBuilder.createQuery(CharacterSheetRoll.class);
    Root<CharacterSheetRoll> root = criteria.from(CharacterSheetRoll.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetRoll_.sheet), sheet)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<CharacterSheetRoll> listBySheetAndUser(CharacterSheet sheet, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetRoll> criteria = criteriaBuilder.createQuery(CharacterSheetRoll.class);
    Root<CharacterSheetRoll> root = criteria.from(CharacterSheetRoll.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(CharacterSheetRoll_.sheet), sheet),
          criteriaBuilder.equal(root.get(CharacterSheetRoll_.user), user)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

}
