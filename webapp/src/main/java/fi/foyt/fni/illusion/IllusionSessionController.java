package fi.foyt.fni.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionSessionDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionSessionParticipantDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionSession;
import fi.foyt.fni.session.SessionController;

@Named
@SessionScoped
@Stateful
public class IllusionSessionController {
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  @DAO
  private IllusionSessionDAO illusionSessionDAO;
  
  @Inject
  @DAO
  private IllusionSessionParticipantDAO illusionSessionParticipantDAO;
  
  public List<IllusionSession> getEnterableSessions() {
    return illusionSessionParticipantDAO.listSessionByParticipant(sessionController.getLoggedUser());
  }

  public IllusionSession getSession() {
    if (illusionSessionId != null)
      return illusionSessionDAO.findById(illusionSessionId);
    
    return null;
  }
  
  public IllusionSession findSessionById(Long illusionSessionId) {
    return illusionSessionDAO.findById(illusionSessionId);
  }
  
  public String enterSession(IllusionSession illusionSession) {
    this.illusionSessionId = illusionSession.getId();
    return "/illusion/index.jsf?faces-redirect=true";
  }

  public void leaveSession() {
    illusionSessionId = null;
  }
  
  private Long illusionSessionId;
}
