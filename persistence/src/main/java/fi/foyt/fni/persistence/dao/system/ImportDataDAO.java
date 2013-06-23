package fi.foyt.fni.persistence.dao.system;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.system.ImportData_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.system.ImportData;
import fi.foyt.fni.persistence.model.system.ImportDataState;

@RequestScoped
@DAO
public class ImportDataDAO extends GenericDAO<ImportData> {

	public ImportData create(String strategy, ImportDataState state, byte[] data) {
    EntityManager entityManager = getEntityManager();

    ImportData importData = new ImportData();
    importData.setData(data);
    importData.setStrategy(strategy);
    importData.setState(state);

    entityManager.persist(importData);

    return importData;
  }

  public List<ImportData> listByState(ImportDataState state, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ImportData> criteria = criteriaBuilder.createQuery(ImportData.class);
    Root<ImportData> root = criteria.from(ImportData.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(ImportData_.state), state)
    );
    
    TypedQuery<ImportData> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public ImportData updateData(ImportData importData, byte[] data) {
    EntityManager entityManager = getEntityManager();

    importData.setData(data);
    
    importData = entityManager.merge(importData);
    return importData;
  }

  public ImportData updateState(ImportData importData, ImportDataState state) {
    EntityManager entityManager = getEntityManager();

    importData.setState(state);
    
    importData = entityManager.merge(importData);
    return importData;
  }
}
