package fi.foyt.fni.illusion;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.servlet.config.Domain;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.PathAndQuery;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Substitute;

import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.system.SystemSettingsController;

@RewriteConfiguration
public class IllusionCustomDomainRewriteConfigurationProvider extends HttpConfigurationProvider {

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private IllusionEventController illusionEventController;
  
  @Override
  public Configuration getConfiguration(ServletContext context) {
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();
    
    for (IllusionEvent event : illusionEventController.listIllusionEventsWithDomain()) {
      addCustomDomainForwards(configuration, event.getDomain(), event.getUrlName());
      String eventUrl = systemSettingsController.getHostUrl(event.getDomain(), false, true);
      
      configuration.addRule()
        .when(Direction.isOutbound()
          .and(PathAndQuery.matches("/illusion/event.jsf?urlName=" + event.getUrlName()))
        )
        .perform(Substitute.with(eventUrl)
          .and(Log.message(Level.DEBUG, String.format("Event to custom domain outbound substitute %s", eventUrl)))
        );
    }
    
    String siteHost = systemSettingsController.getSiteHost();
    String siteUrl = systemSettingsController.getSiteUrl(false, true);

    addOutboundSiteRule(configuration, siteHost, siteUrl, "/index.jsf", "/");
    addOutboundSiteRule(configuration, siteHost, siteUrl, "/forge/index.jsf", "/forge");
    addOutboundSiteRule(configuration, siteHost, siteUrl, "/illusion/index.jsf", "/illusion");
    addOutboundSiteRule(configuration, siteHost, siteUrl, "/gamelibrary/index.jsf", "/gamelibrary");
    addOutboundSiteRule(configuration, siteHost, siteUrl, "/forum/index.jsf", "/forum");
    addOutboundSiteRule(configuration, siteHost, siteUrl, "/about.jsf", "/about");
    
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
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-edit-page.jsf", "edit-page", null, new String[] { "pageId" });
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-material.jsf", "materials/{materialPath}", new String[] { "materialPath" }, null);
    addDomainRule(configuration, domain, eventUrl, "/illusion/event-page.jsf", "pages/{materialPath}", new String[] { "materialPath" }, null);
    
  }
  
  private void addDomainRule(ConfigurationBuilder configuration, String domain, String eventUrl, String jsfRule, String page) {
    addDomainRule(configuration, domain, eventUrl, jsfRule, page, null, null);
  }
  
  private void addDomainRule(ConfigurationBuilder configuration, String domain, String eventUrl, String jsfRule, String page, String[] pathParams, String[] queryParams) {
    String path = "/" + page;
    addInboundDomainRule(configuration, domain, eventUrl, page, path);
    addOutboundDomainRule(configuration, domain, jsfRule, path, pathParams, queryParams);
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

  private void addOutboundDomainRule(ConfigurationBuilder configuration, String domain, String jsfRule, String path, String[] pathParams, String[] queryParams) {
    ConditionBuilder when = Direction.isOutbound()
      .and(Domain.matches(domain))
      .and(Path.matches(jsfRule));
    
    if (pathParams != null) {
      for (String pathParam : pathParams) {
        when = when.and(Query.parameterExists(pathParam));
      }
    }
    
    if (queryParams != null) {
      for (String queryParam : queryParams) {
        when = when.and(Query.parameterExists(queryParam));
      }
    }
    
    StringBuilder locationBuilder = new StringBuilder(path);
    if (queryParams != null) {
      for (int i = 0, l = queryParams.length; i < l; i++) {
        String queryParam = queryParams[i];
        locationBuilder
          .append(i == 0 ? '?' : '&')
          .append(queryParam)
          .append("={")
          .append(queryParam)
          .append("}");
      }
    }
    
    configuration.addRule()
      .when(when)
      .perform(Substitute.with(locationBuilder.toString()).and(Log.message(Level.DEBUG, String.format("Custom domain outbound substitute %s -> %s", jsfRule, locationBuilder.toString()))))
      .withPriority(0);
  }
  
  private void addOutboundSiteRule(ConfigurationBuilder configuration, String siteHost, String siteUrl, String jsfRule, String path) {
    configuration.addRule()
      .when(Direction.isOutbound()
        .andNot(Domain.matches(siteHost))
        .and(jsfRule.contains("?") ? PathAndQuery.matches(jsfRule) : Path.matches(jsfRule))
      )
      .perform(Substitute.with(siteUrl + path).and(Log.message(Level.DEBUG, String.format("Site outbound substitute %s -> %s", jsfRule, siteUrl + path))))
      .withPriority(0);
  }
 
}
