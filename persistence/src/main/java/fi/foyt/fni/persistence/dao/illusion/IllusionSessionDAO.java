package fi.foyt.fni.persistence.dao.illusion;

import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionSession;

@DAO
public class IllusionSessionDAO extends GenericDAO<IllusionSession> {

	private static final long serialVersionUID = 1L;

	public IllusionSession create(String name) {
    EntityManager entityManager = getEntityManager();

    IllusionSession illusionSession = new IllusionSession();
    illusionSession.setName(name);
    
    entityManager.persist(illusionSession);

    return illusionSession;
  }
  
}
