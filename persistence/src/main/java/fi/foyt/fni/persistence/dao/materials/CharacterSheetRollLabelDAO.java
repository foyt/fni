package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRollLabel;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRollLabel_;

public class CharacterSheetRollLabelDAO extends GenericDAO<CharacterSheetRollLabel> {

	private static final long serialVersionUID = 1L;

	public CharacterSheetRollLabel create(CharacterSheet sheet, String label) {
    CharacterSheetRollLabel characterSheetData = new CharacterSheetRollLabel();

    characterSheetData.setLabel(label);
    characterSheetData.setSheet(sheet);

    return persist(characterSheetData);
  }
  
  public CharacterSheetRollLabel findBySheetAndLabel(CharacterSheet sheet, String label) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetRollLabel> criteria = criteriaBuilder.createQuery(CharacterSheetRollLabel.class);
    Root<CharacterSheetRollLabel> root = criteria.from(CharacterSheetRollLabel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetRollLabel_.sheet), sheet),
        criteriaBuilder.equal(root.get(CharacterSheetRollLabel_.label), label)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
	
  public List<CharacterSheetRollLabel> listBySheet(CharacterSheet sheet) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CharacterSheetRollLabel> criteria = criteriaBuilder.createQuery(CharacterSheetRollLabel.class);
    Root<CharacterSheetRollLabel> root = criteria.from(CharacterSheetRollLabel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CharacterSheetRollLabel_.sheet), sheet)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

}
