package fi.foyt.fni.persistence.dao.illusion;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionSession;

@RequestScoped
@DAO
public class IllusionSessionDAO extends GenericDAO<IllusionSession> {

	public IllusionSession create(String name) {
    EntityManager entityManager = getEntityManager();

    IllusionSession illusionSession = new IllusionSession();
    illusionSession.setName(name);
    
    entityManager.persist(illusionSession);

    return illusionSession;
  }
  
}
