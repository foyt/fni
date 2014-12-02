package fi.foyt.fni.illusion;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.neuland.jade4j.template.TemplateLoader;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventTemplate;
import fi.foyt.fni.utils.servlet.RequestUtils;

public class IllusionJadeTemplateLoader implements TemplateLoader {
  
  @Inject
  private IllusionEventController illusionEventController; 
  
  @Override
  public long getLastModified(String name) throws IOException {
    IllusionEventTemplate template = getTemplate(name);
    if (template != null) {
      return template.getModified().getTime();
    }
    
    return -1;
  }

  @Override
  public Reader getReader(String name) throws IOException {
    IllusionEventTemplate template = getTemplate(name);
    if (template != null) {
      return new StringReader(template.getData());
    }
    
    return null;
  }

  private IllusionEventTemplate getTemplate(String name) {
    if (StringUtils.isNotBlank(name)) {
      String eventUrl = RequestUtils.extractToNextSlash(name);
      String templateName = name.substring(eventUrl.length() + 1);
      if (StringUtils.isNotBlank(eventUrl)) {
        IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(eventUrl);
        if (illusionEvent != null) {
          IllusionEventTemplate template = illusionEventController.findEventTemplate(illusionEvent, templateName);
          if (template != null) {
            return template;
          }
        }
        
        return illusionEventController.findRootTemplate(templateName);
      }
    }
    
    return null;
  }
  
}
