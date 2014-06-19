package fi.foyt.fni.coops;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.CoOpsSessionDAO;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSessionType;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class CoOpsSessionController {
  
  private static final long SESSION_TIMEOUT = 10 * 1000;
  
  @Inject
  private Event<CoOpsSessionCloseEvent> sessionCloseEvent;
  
  @Inject
  private CoOpsSessionDAO coOpsSessionDAO;
  
  public CoOpsSession createSession(Material material, User user, CoOpsSessionType type, String algorithm, Long joinRevision) {
    String sessionId = UUID.randomUUID().toString();
    CoOpsSession session = coOpsSessionDAO.create(material, user, sessionId, type, Boolean.FALSE, algorithm, joinRevision, new Date());
    return session;
  }
  
  public CoOpsSession findSessionBySessionId(String sessionId) {
    return coOpsSessionDAO.findBySessionId(sessionId);
  }
  
  public List<CoOpsSession> listTimedoutSessions() {
    Date accessedBefore = new Date(System.currentTimeMillis() - SESSION_TIMEOUT);
    return coOpsSessionDAO.listByAccessedBeforeAndTypeAndClosed(accessedBefore, CoOpsSessionType.REST, Boolean.FALSE);
  }

  public List<CoOpsSession> listSessionsByClosed(Boolean closed) {
    return coOpsSessionDAO.listByClosed(closed);
  }

  public List<CoOpsSession> listSessionsByUserAndClosed(User user, Boolean closed) {
    return coOpsSessionDAO.listByUserAndClosed(user, closed);
  }
  
  public List<CoOpsSession> listSessionsByMaterialAndClosed(Material material, Boolean closed) {
    return coOpsSessionDAO.listByMaterialAndClosed(material, closed);
  }
  
  public CoOpsSession updateSessionType(CoOpsSession coOpsSession, CoOpsSessionType type) {
    return coOpsSessionDAO.updateType(coOpsSession, type);
  }

  public void closeSession(CoOpsSession session) {
    closeSession(session, false);
  }
  
  public void closeSession(CoOpsSession session, boolean quiet) {
    coOpsSessionDAO.updateClosed(session, Boolean.TRUE);
    if (!quiet) {
      sessionCloseEvent.fire(new CoOpsSessionCloseEvent(session.getSessionId()));
    }
  }
}
