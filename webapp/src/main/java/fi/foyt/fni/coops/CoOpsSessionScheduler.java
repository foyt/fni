package fi.foyt.fni.coops;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import fi.foyt.fni.materials.CoOpsSessionController;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;

@Singleton
public class CoOpsSessionScheduler {

  @Inject
  private CoOpsSessionController coOpsSessionController;
  
  @Schedule(second = "*/15", minute = "*", hour = "*", persistent = false)
  public void sessionCloseScheduler() {
    List<CoOpsSession> timedoutSessions = coOpsSessionController.listTimedoutSessions();
    for (CoOpsSession timedoutSession : timedoutSessions) {
      coOpsSessionController.closeSession(timedoutSession);
    }
  }
  
}
