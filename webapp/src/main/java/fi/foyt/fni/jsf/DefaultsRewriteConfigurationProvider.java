package fi.foyt.fni.jsf;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class DefaultsRewriteConfigurationProvider extends HttpConfigurationProvider {
  
  @Override
  public Configuration getConfiguration(ServletContext context) {
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();

    configuration
      .addRule(Join.path("/forge").to("/forge/index.jsf"))
      .addRule(Join.path("/gamelibrary").to("/gamelibrary/index.jsf"));
    
    configuration.addRule()
      .when(Direction.isInbound().and(Path.matches("/login")).and(Query.matches("{query}")))
      .perform(Redirect.temporary(context.getContextPath() + "/login/?{query}"));
    
    configuration.addRule()
      .when(Direction.isInbound().and(Path.matches("/logout")))
      .perform(new HttpOperation() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context) {
           event.getRequest().getSession().invalidate();
         }
      }.and(Redirect.temporary(context.getContextPath() + "/")));
    
    configuration.addRule()
      .when(Path.matches("/theme/{file}"))
      .perform(Substitute.with("/javax.faces.resource/{file}.jsf?ln=novus"))
      .where("file").matches("[a-zA-Z0-9/_.\\-]*");
    
    configuration.addRule(Join.path("/scripts/{file}").to("/faces/javax.faces.resource/scripts/{file}"))
      .where("file").matches("[a-zA-Z0-9/_.\\-]*");
    
    return configuration;
  }

  @Override
  public int priority() {
    return 1000;
  }
  
}
