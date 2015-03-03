package fi.foyt.fni.monitoring;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
@Named
@Stateful
public class NewRelicBackingBean {

  @Inject
  private Logger logger;
  
  @PostConstruct
  public void init() {
    String snippetFilePath = System.getProperty("newrelic.browser.snippet");
    if (StringUtils.isNotBlank(snippetFilePath)) {
      File snippetFile = new File(snippetFilePath);
      if (snippetFile.exists()) {
        try {
          snippet = FileUtils.readFileToString(snippetFile);
        } catch (IOException e) {
          logger.log(Level.SEVERE, String.format("Error reading snippet file '%s'", snippetFilePath), e);
        }
      } else {
        logger.severe(String.format("Snippet file '%s' does not exist", snippetFilePath));
      }
    }
    
    enabled = StringUtils.isNotBlank(snippet);
  }
  
  public boolean getEnabled() {
    return enabled;
  }
  
  public String getSnippet() {
    return snippet;
  }
  
  private boolean enabled;
  private String snippet;
}
