package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheetData;
import fi.foyt.fni.persistence.model.materials.CharacterSheetData_;
import fi.foyt.fni.persistence.model.materials.CharacterSheetEntry;
import fi.foyt.fni.persistence.model.users.User;

public class CharacterSheetDataDAO extends GenericDAO<CharacterSheetData> {

	private static final long serialVersionUID = 1L;

	public CharacterSheetData create(CharacterSheetEntry entry, User user, String value) {
    CharacterSheetData characterSheetData = new CharacterSheetData();
    characterSheetData.setEntry(entry);
    characterSheetData.setUser(user);
    characterSheetData.setValue(value);
    
    return persist(characterSheetData);
  }
	
  public CharacterSheetData findByEntryAndUser(CharacterSheetEntry entry, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetData> criteria = criteriaBuilder.createQuery(CharacterSheetData.class);
    Root<CharacterSheetData> root = criteria.from(CharacterSheetData.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetData_.entry), entry),
        criteriaBuilder.equal(root.get(CharacterSheetData_.user), user)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<CharacterSheetData> listByEntry(CharacterSheetEntry entry) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetData> criteria = criteriaBuilder.createQuery(CharacterSheetData.class);
    Root<CharacterSheetData> root = criteria.from(CharacterSheetData.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetData_.entry), entry)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public CharacterSheetData updateValue(CharacterSheetData characterSheetData, String value) {
    characterSheetData.setValue(value);
    return persist(characterSheetData);
  }
	
}
