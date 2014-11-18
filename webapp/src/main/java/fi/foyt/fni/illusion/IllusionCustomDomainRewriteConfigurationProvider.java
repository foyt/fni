package fi.foyt.fni.illusion;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.servlet.config.Domain;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.PathAndQuery;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

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

    String contextPath = systemSettingsController.getSiteContextPath();
    Integer httpPort = systemSettingsController.getSiteHttpPort();
    String port = httpPort != 80 ? ":" + httpPort : "";
    
    addCustomDomainForwards(configuration);
    
    configuration.addRule()
      .when(Direction.isOutbound()
        .and(Path.matches("/illusion/event.jsf"))
        .and(Query.parameterExists("urlName"))
        .and(new EventUrlNameRule("urlName"))
      )
      .perform(Substitute.with(String.format("http://{eventDomain}%s%s", port, contextPath))
        .and(Log.message(Level.DEBUG, String.format("Event to custom domain outbound substitute %s", String.format("http://{eventDomain}%s%s", port, contextPath))))    
      );
    
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
  
  private void addCustomDomainForwards(ConfigurationBuilder configuration) {
    addDomainRule(configuration, "/illusion/event.jsf", "");
    addDomainRule(configuration, "/illusion/event-manage-pages.jsf", "manage-pages");
    addDomainRule(configuration, "/illusion/event-groups.jsf", "groups");
    addDomainRule(configuration, "/illusion/event-materials.jsf", "materials");
    addDomainRule(configuration, "/illusion/event-participants.jsf", "participants");
    addDomainRule(configuration, "/illusion/event-payment.jsf", "payment");
    addDomainRule(configuration, "/illusion/event-settings.jsf", "settings");
    addDomainRule(configuration, "/illusion/dojoin.jsf", "dojoin");
    addDomainRule(configuration, "/illusion/event-edit-page.jsf", "edit-page", null, new String[] { "pageId" });
    addDomainRule(configuration, "/illusion/event-material.jsf", "materials/{materialPath}", new String[] { "materialPath" }, null);
    addDomainRule(configuration, "/illusion/event-page.jsf", "pages/{materialPath}", new String[] { "materialPath" }, null);
  }
  
  private void addDomainRule(ConfigurationBuilder configuration, String jsfRule, String page) {
    addDomainRule(configuration, jsfRule, page, null, null);
  }
  
  private void addDomainRule(ConfigurationBuilder configuration, String jsfRule, String page, String[] pathParams, String[] queryParams) {
    String path = "/" + page;
    addInboundDomainRule(configuration, page, path);
    addOutboundDomainRule(configuration, jsfRule, path, pathParams, queryParams);
  }

  private void addInboundDomainRule(ConfigurationBuilder configuration, String page, String path) {
    String substitute = "/illusion/event/{eventUrlName}" + (page.equals("") ? "" : "/" + page);
    configuration.addRule()
      .when(
          Direction.isInbound()
            .and(Path.matches(path))
            .and(new EventCustomDomainRule())
      )
      .perform(Substitute.with(substitute).and(Log.message(Level.DEBUG, String.format("Custom domain inbound substitute %s -> %s", path, substitute))));
  }

  private void addOutboundDomainRule(ConfigurationBuilder configuration, String jsfRule, String path, String[] pathParams, String[] queryParams) {
    ConditionBuilder when = Direction.isOutbound()
      .and(new EventCustomDomainRule())
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

  // TODO: This is too slow for production
  private class EventCustomDomainRule extends HttpCondition implements Parameterized {

    @Override
    public Set<String> getRequiredParameterNames() {
      Set<String> result = new HashSet<>();
      result.add("eventUrlName");
      result.add("domain");
      return result;
    }

    @Override
    public void setParameterStore(ParameterStore store) {
      this.parameterStore = store;
    }

    @Override
    public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context) {
      String hostName = null;

      if (event instanceof HttpOutboundServletRewrite) {
        hostName = event.getAddress().getDomain();
        if (hostName == null) {
          hostName = event.getRequest().getServerName();
        }
      } else {
        hostName = event.getRequest().getServerName();
      }
      
      if (hostName != null) {
        IllusionEvent illusionEvent = illusionEventController.findIllusionEventByDomain(hostName);
        if (illusionEvent != null) {
          ParameterValueStore parameterValueStore = (ParameterValueStore) context.get(ParameterValueStore.class);
          parameterValueStore.submit(event, context, parameterStore.get("eventUrlName"), illusionEvent.getUrlName());
          return true;
        }
      }
      
      return false;
    }
    
    private ParameterStore parameterStore;
  }
  
  // TODO: This is too slow for production
  private class EventUrlNameRule extends HttpCondition implements Parameterized {

    public EventUrlNameRule(String paramName) {
      this.paramName = paramName;
    }
    
    @Override
    public Set<String> getRequiredParameterNames() {
      Set<String> result = new HashSet<>();
      result.add("eventUrlName");
      result.add("domain");
      return result;
    }

    @Override
    public void setParameterStore(ParameterStore store) {
      this.parameterStore = store;
    }

    @Override
    public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context) {
      ParameterValueStore parameterValueStore = (ParameterValueStore) context.get(ParameterValueStore.class);
      String urlName = parameterValueStore.retrieve(parameterStore.get(paramName));
      
      if (urlName != null) {
        IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(urlName);
        if (illusionEvent != null && illusionEvent.getDomain() != null) {
          parameterValueStore.submit(event, context, parameterStore.get("eventDomain"), illusionEvent.getDomain());
          return true;
        }
      }
      
      return false;
    }
    
    private ParameterStore parameterStore;
    private String paramName;
  }
 
}
