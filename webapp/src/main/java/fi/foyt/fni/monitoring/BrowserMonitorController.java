package fi.foyt.fni.monitoring;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class BrowserMonitorController {
  
  @Inject
  private Logger logger;
  
  @PostConstruct
  public void init() {
    String browserMonitorScriptPath = System.getProperty("browser.monitor.script");
    if (StringUtils.isBlank(browserMonitorScriptPath)) {
      browserMonitorScriptPath = System.getProperty("newrelic.browser.snippet");
    }
    
    if (StringUtils.isNotBlank(browserMonitorScriptPath)) {
      File snippetFile = new File(browserMonitorScriptPath);
      if (snippetFile.exists()) {
        try {
          monitoringScript = FileUtils.readFileToString(snippetFile);
        } catch (IOException e) {
          logger.log(Level.SEVERE, String.format("Error reading browser monitor script file '%s'", monitoringScript), e);
        }
      } else {
        logger.severe(String.format("Browser monitor script file '%s' does not exist", monitoringScript));
      }
    }
  }

  public String getMonitoringScript() {
    return monitoringScript;
  }
  
  private String monitoringScript;
}
