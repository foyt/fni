package fi.foyt.fni.coops;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.materials.CoOpsSession;

/**
 * Scheduler for removing closed CoOps sessions
 * 
 * @author Antti Leppä
 */
@Startup
@Singleton
public class CoOpsSessionScheduler {
  
  private static final int TIMER_INITIAL = 10000;
  private static final int TIMER_INTERVAL = 1000;
  
  @Inject
  private CoOpsSessionController coOpsSessionController;

  @Resource
  private TimerService timerService;

  private CoOpsSessionScheduler() {
  }
  
  /**
   * Post construct method for this bean
   */
  @PostConstruct
  public void init() {
    startTimer(TIMER_INITIAL);
  }
  
  /**
   * Timeout method
   * 
   * @param timer timer instance
   */
  @Timeout
  public void timeout(Timer timer) {
    List<CoOpsSession> timedoutSessions = coOpsSessionController.listTimedoutSessions();
    for (CoOpsSession timedoutSession : timedoutSessions) {
      coOpsSessionController.closeSession(timedoutSession);
    }
    
    startTimer(TIMER_INTERVAL);
  }
  
  private void startTimer(int duration) {
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerService.createSingleActionTimer(duration, timerConfig);
  }
  
}
