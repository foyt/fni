package fi.foyt.fni.view;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class ViewControllerMapper {

	@Inject
  private Instance<ViewController> viewControllers; 
	
	@Inject
  private Logger logger;

  @SuppressWarnings("unchecked")
	public ViewController getViewController(String controllerName) {
  	Class<ViewController> controllerClass;
		try {
			String className = (String) mapping.get(controllerName);
			if (StringUtils.isNotBlank(className)) {
  			controllerClass = (Class<ViewController>) Class.forName(className);
  			if (controllerClass != null)
  			  return viewControllers.select(controllerClass).get();
			}
		} catch (ClassNotFoundException e) {
		}

		return null;
  }

  @PostConstruct
  private void load() {
    try {
      logger.info("Reading view controller mapping");
    
    	mapping = new Properties();
  
      InputStream viewControllersStream = getClass().getResourceAsStream("/viewcontrollers.properties");
			mapping.load(viewControllersStream);

      logger.info("View controller mappings read");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Mapping reading failed", e);
      throw new ExceptionInInitializerError(e);
    } 

  }

  private Properties mapping;
}