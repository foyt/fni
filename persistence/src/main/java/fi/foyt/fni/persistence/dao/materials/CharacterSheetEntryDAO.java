package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.CharacterSheetEntry;
import fi.foyt.fni.persistence.model.materials.CharacterSheetEntryType;
import fi.foyt.fni.persistence.model.materials.CharacterSheetEntry_;

public class CharacterSheetEntryDAO extends GenericDAO<CharacterSheetEntry> {

	private static final long serialVersionUID = 1L;

	public CharacterSheetEntry create(CharacterSheet sheet, String name, CharacterSheetEntryType type) {
    CharacterSheetEntry characterSheetEntry = new CharacterSheetEntry();
    characterSheetEntry.setName(name);
    characterSheetEntry.setSheet(sheet);
    characterSheetEntry.setType(type);
    
    return persist(characterSheetEntry);
  }
	
  public List<CharacterSheetEntry> listBySheet(CharacterSheet sheet) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetEntry> criteria = criteriaBuilder.createQuery(CharacterSheetEntry.class);
    Root<CharacterSheetEntry> root = criteria.from(CharacterSheetEntry.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(CharacterSheetEntry_.sheet), sheet));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public CharacterSheetEntry findBySheetAndName(CharacterSheet sheet, String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetEntry> criteria = criteriaBuilder.createQuery(CharacterSheetEntry.class);
    Root<CharacterSheetEntry> root = criteria.from(CharacterSheetEntry.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetEntry_.sheet), sheet),
        criteriaBuilder.equal(root.get(CharacterSheetEntry_.name), name)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public CharacterSheetEntry updateType(CharacterSheetEntry entry, CharacterSheetEntryType type) {
    entry.setType(type);
    return persist(entry);
  }
	
}
