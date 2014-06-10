package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSessionType;
import fi.foyt.fni.persistence.model.materials.CoOpsSession_;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;

public class CoOpsSessionDAO extends GenericDAO<CoOpsSession> {
  
  private static final long serialVersionUID = -5793299154748976020L;

  public CoOpsSession create(Material material, User user, String sessionId, CoOpsSessionType type, Boolean closed, String algorithm, Long joinRevision, Date accessed) {
    CoOpsSession coOpsSession = new CoOpsSession();
    
    coOpsSession.setAlgorithm(algorithm);
    coOpsSession.setJoinRevision(joinRevision);
    coOpsSession.setMaterial(material);
    coOpsSession.setUser(user);
    coOpsSession.setSessionId(sessionId);
    coOpsSession.setType(type);
    coOpsSession.setClosed(closed);
    coOpsSession.setAccessed(accessed);

    return persist(coOpsSession);
  }

  public CoOpsSession findBySessionId(String sessionId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CoOpsSession> criteria = criteriaBuilder.createQuery(CoOpsSession.class);
    Root<CoOpsSession> root = criteria.from(CoOpsSession.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(CoOpsSession_.sessionId), sessionId)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<CoOpsSession> listByClosed(Boolean closed) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CoOpsSession> criteria = criteriaBuilder.createQuery(CoOpsSession.class);
    Root<CoOpsSession> root = criteria.from(CoOpsSession.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(CoOpsSession_.closed), closed)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<CoOpsSession> listByMaterialAndClosed(Material material, Boolean closed) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CoOpsSession> criteria = criteriaBuilder.createQuery(CoOpsSession.class);
    Root<CoOpsSession> root = criteria.from(CoOpsSession.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CoOpsSession_.closed), closed),
        criteriaBuilder.equal(root.get(CoOpsSession_.material), material)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<CoOpsSession> listByAccessedBeforeAndTypeAndClosed(Date accessed, CoOpsSessionType type, Boolean closed) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CoOpsSession> criteria = criteriaBuilder.createQuery(CoOpsSession.class);
    Root<CoOpsSession> root = criteria.from(CoOpsSession.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(CoOpsSession_.closed), closed),
        criteriaBuilder.equal(root.get(CoOpsSession_.type), type),
        criteriaBuilder.lessThan(root.get(CoOpsSession_.accessed), accessed)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public CoOpsSession updateClosed(CoOpsSession coOpsSession, Boolean closed) {
    coOpsSession.setClosed(closed);
    return persist(coOpsSession);
  }

  public CoOpsSession updateType(CoOpsSession coOpsSession, CoOpsSessionType type) {
    coOpsSession.setType(type);
    return persist(coOpsSession);
  }
  
}
