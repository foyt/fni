package fi.foyt.fni;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletCtxListener implements ServletContextListener {
	
	@Inject
	private Logger logger;
	
  public void contextInitialized(ServletContextEvent event) {
    ServletContext servletContext = event.getServletContext();
        
    String webappPath = servletContext.getRealPath("/");
    logger.info("Setting webappPath to " + webappPath);
    System.setProperty("webappPath", webappPath);
    System.setProperty("fni-context-path", servletContext.getContextPath());
    logger.info("Forge & Illusion cloud started");
  }

  public void contextDestroyed(ServletContextEvent event) {
    logger.info("Forge & Illusion cloud stopped");
  }
}
