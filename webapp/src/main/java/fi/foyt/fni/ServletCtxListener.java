package fi.foyt.fni;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.system.SystemSettingsController;

@WebListener
public class ServletCtxListener implements ServletContextListener {
	
	@Inject
	private Logger logger;
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
  public void contextInitialized(ServletContextEvent event) {
    ServletContext servletContext = event.getServletContext();
        
    String webappPath = servletContext.getRealPath("/");
    logger.info("Setting webappPath to " + webappPath);
    System.setProperty("webappPath", webappPath);
    System.setProperty("fni-context-path", servletContext.getContextPath());
    
    checkMandatorySetting("site host", systemSettingsController.getSiteHost());
    checkMandatorySetting("site http port", systemSettingsController.getSiteHttpPort());
    checkMandatorySetting("site https port", systemSettingsController.getSiteHttpsPort());
    checkMandatorySetting("site context path", systemSettingsController.getSiteContextPath());

    logger.info("Forge & Illusion cloud started");
  }

  private void checkMandatorySetting(String name, Integer setting) {
    if (setting == null) {
      throw new ExceptionInInitializerError("Mandatory setting '" + name + "' is missing");
    }
  }

  private void checkMandatorySetting(String name, String setting) {
    if (StringUtils.isBlank(setting)) {
      throw new ExceptionInInitializerError("Mandatory setting '" + name + "' is missing");
    }
  }

  public void contextDestroyed(ServletContextEvent event) {
    logger.info("Forge & Illusion cloud stopped");
  }
}
