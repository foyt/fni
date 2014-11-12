package fi.foyt.fni.illusion;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.servlet.config.Domain;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.PathAndQuery;
import org.ocpsoft.rewrite.servlet.config.Substitute;

import fi.foyt.fni.persistence.model.illusion.IllusionEvent;

@RewriteConfiguration
public class IllusionCustomDomainRewriteConfigurationProvider extends HttpConfigurationProvider {
  
  @Inject
  private IllusionEventController illusionEventController;
  
  @Override
  public Configuration getConfiguration(ServletContext context) {
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();
    
    for (IllusionEvent event : illusionEventController.listIllusionEventsWithDomain()) {
      addCustomDomainForwards(configuration, event.getDomain(), event.getUrlName());
    }
    
    return configuration;
  }

  @Override
  public int priority() {
    return 10;
  }
  
  private void addCustomDomainForwards(ConfigurationBuilder configuration, String domain, String eventUrlName) {
    String eventUrl = new StringBuilder("/illusion/event/").append(eventUrlName).toString();

    addDomainRule(configuration, domain, eventUrl, "/illusion/event.jsf", "");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-manage-pages.jsf", "manage-pages");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-groups.jsf", "groups");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-materials.jsf", "materials");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-participants.jsf", "participants");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-payment.jsf", "payment");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-settings.jsf", "settings");
    addDomainRule(configuration, domain, eventUrl, "/illusion/dojoin.jsf", "dojoin");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-edit-page.jsf", "edit-page");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-material.jsf?materialPath={materialPath}", "materials/{materialPath}");
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-page.jsf?materialPath={materialPath}", "pages/{materialPath}");
  }

  private void addDomainRule(ConfigurationBuilder configuration, String domain, String eventUrl, String jsfRule, String page) {
    String path = "/" + page;
    addInboundDomainRule(configuration, domain, eventUrl, page, path);
    addOutboundDomainRule(configuration, domain, jsfRule, path);
  }

  private void addInboundDomainRule(ConfigurationBuilder configuration, String domain, String eventUrl, String page, String path) {
    StringBuilder forwardToBuilder = new StringBuilder(eventUrl);
    if (!"".equals(page)) {
      forwardToBuilder.append('/');
    }
    
    forwardToBuilder.append(page);
    String forwardTo = forwardToBuilder.toString();
    
    configuration.addRule()
    .when(
        Direction.isInbound()
          .and(Domain.matches(domain))
          .and(Path.matches(path))
    )
    .perform(Substitute.with(forwardTo).and(Log.message(Level.DEBUG, String.format("Custom domain inbound substitute %s -> %s", path, forwardTo))));
  }

  private void addOutboundDomainRule(ConfigurationBuilder configuration, String domain, String jsfRule, String path) {
    configuration.addRule()
      .when(Direction.isOutbound()
        .and(Domain.matches(domain))
        .and(jsfRule.contains("?") ? PathAndQuery.matches(jsfRule) : Path.matches(jsfRule))
      )
      .perform(Substitute.with(path).and(Log.message(Level.DEBUG, String.format("Custom domain outbound substitute %s -> %s", jsfRule, path))))
      .withPriority(0);
  }
 
}
