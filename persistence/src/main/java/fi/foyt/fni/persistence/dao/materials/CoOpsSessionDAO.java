package fi.foyt.fni.persistence.dao.materials;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSession_;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;

public class CoOpsSessionDAO extends GenericDAO<CoOpsSession> {
  
  private static final long serialVersionUID = -5793299154748976020L;

  public CoOpsSession create(Material material, User user, String sessionId, String algorithm, Long joinRevision) {
    CoOpsSession coOpsSession = new CoOpsSession();
    
    coOpsSession.setAlgorithm(algorithm);
    coOpsSession.setJoinRevision(joinRevision);
    coOpsSession.setMaterial(material);
    coOpsSession.setUser(user);
    coOpsSession.setSessionId(sessionId);

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
  
}
