package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRoll;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRollLabel;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRoll_;
import fi.foyt.fni.persistence.model.users.User;

public class CharacterSheetRollDAO extends GenericDAO<CharacterSheetRoll> {

	private static final long serialVersionUID = 1L;

	public CharacterSheetRoll create(CharacterSheetRollLabel label, String roll, User user, Date time, Integer result) {
    CharacterSheetRoll characterSheetRollResult = new CharacterSheetRoll();

    characterSheetRollResult.setUser(user);
    characterSheetRollResult.setResult(result);
    characterSheetRollResult.setLabel(label);
    characterSheetRollResult.setRoll(roll);
    characterSheetRollResult.setTime(time);
    

    return persist(characterSheetRollResult);
  }
	
  public List<CharacterSheetRoll> listByLabel(CharacterSheetRollLabel label) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetRoll> criteria = criteriaBuilder.createQuery(CharacterSheetRoll.class);
    Root<CharacterSheetRoll> root = criteria.from(CharacterSheetRoll.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetRoll_.label), label)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<CharacterSheetRoll> listByRollAndUser(CharacterSheetRollLabel label, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetRoll> criteria = criteriaBuilder.createQuery(CharacterSheetRoll.class);
    Root<CharacterSheetRoll> root = criteria.from(CharacterSheetRoll.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetRoll_.label), label),
        criteriaBuilder.equal(root.get(CharacterSheetRoll_.user), user)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
}
