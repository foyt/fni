package fi.foyt.fni.coops;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.Transactional;

import fi.foyt.fni.persistence.model.materials.CoOpsSession;

@WebListener
public class CoOpsSessionCloseServletContextListener implements ServletContextListener {
	
	@Inject
	private CoOpsSessionController coOpsSessionController;
	
  @Override
	@Transactional
  public void contextInitialized(ServletContextEvent event) {
    List<CoOpsSession> openSessions = coOpsSessionController.listSessionsByClosed(Boolean.FALSE);
    for (CoOpsSession openSession : openSessions) {
      coOpsSessionController.closeSession(openSession, true);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }
}
