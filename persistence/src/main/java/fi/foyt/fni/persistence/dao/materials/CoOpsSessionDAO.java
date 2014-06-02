package fi.foyt.fni.persistence.dao.materials;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;

public class CoOpsSessionDAO extends GenericDAO<CoOpsSession> {
  
  private static final long serialVersionUID = -5793299154748976020L;

  public CoOpsSession create(Material material, User user, String algorithm, Long joinRevision) {
    CoOpsSession coOpsSession = new CoOpsSession();
    
    coOpsSession.setAlgorithm(algorithm);
    coOpsSession.setJoinRevision(joinRevision);
    coOpsSession.setMaterial(material);
    coOpsSession.setUser(user);
    
    return persist(coOpsSession);
  }
  
}
