package fi.foyt.fni.jsf;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

public class DefaultsRewriteConfigurationProvider extends HttpConfigurationProvider {
  
  @Override
  public Configuration getConfiguration(ServletContext context) {
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();

    configuration
      .addRule(Join.path("/login").to("/users/login.jsf"))
      .addRule(Join.path("/forge").to("/forge/index.jsf"))
      .addRule(Join.path("/gamelibrary").to("/gamelibrary/index.jsf"));

    return configuration;
  }

  @Override
  public int priority() {
    return 1000;
  }
  
}
