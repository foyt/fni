package fi.foyt.fni.coops;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

@Stateless
public class CoOpsSessionEventsController {

  @Inject
  private UserController userController;
  
  public List<Object> createSessionEvents(List<CoOpsSession> sessions, String status) {
    List<Object> result = new ArrayList<Object>();
    
    for (CoOpsSession session : sessions) {
      String sessionId = session.getSessionId();
      User sessionUser = session.getUser();
      String email = userController.getUserPrimaryEmail(sessionUser);
      
      result.add(new CoOpsSessionEvent(sessionId, sessionUser.getFullName(), email, status));
    }
    
    return result;
  }
  
}
