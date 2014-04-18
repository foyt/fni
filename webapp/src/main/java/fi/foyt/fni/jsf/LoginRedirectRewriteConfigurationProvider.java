package fi.foyt.fni.jsf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

public class LoginRedirectRewriteConfigurationProvider extends HttpConfigurationProvider {
  
  @Inject
  private Logger logger;
  
  @Inject
  private NotLoggedInCondition notLoggedInCondition;
  
  @Override
  public Configuration getConfiguration(ServletContext context) {
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();

    try {
      addLoginRequiredPath(context, configuration, "/forge/", "/forge/index.jsf");
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, "Failed to initialize login redirects", e);
    }

    return configuration;
  }

  private void addLoginRequiredPath(ServletContext context, ConfigurationBuilder configuration, String path, String to) throws UnsupportedEncodingException {
    configuration
      .addRule(Join.path(path).to(to))
      .when(DispatchType.isRequest().and(Direction.isInbound()).and(notLoggedInCondition))
      .perform(Redirect.temporary(context.getContextPath() + "/users/login.jsf?redirectUrl=" + URLEncoder.encode(context.getContextPath() + path, "UTF-8")));
  }

  @Override
  public int priority() {
    return 0;
  }
  
}
