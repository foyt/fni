package fi.foyt.fni.illusion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
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
@ApplicationScoped
@Stateful
public class IllusionCustomDomainRewriteConfigurationProvider extends HttpConfigurationProvider {

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @PostConstruct
  public void init() {
    eventCustomDomainUrlNameMap = new HashMap<>();
    eventUrlNameCustomDomainMap = new HashMap<>();
  }
  
  @Inject
  private IllusionEventController illusionEventController;
  
  @Override
  public Configuration getConfiguration(ServletContext context) {
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();

    String contextPath = systemSettingsController.getSiteContextPath();
    Integer httpPort = systemSettingsController.getSiteHttpPort();
    String port = httpPort != 80 ? ":" + httpPort : "";
    String siteHost = systemSettingsController.getSiteHost();
    
    addDomainRule(configuration, siteHost, "/illusion/event.jsf", "");
    addDomainRule(configuration, siteHost, "/illusion/event-manage-pages.jsf", "manage-pages");
    addDomainRule(configuration, siteHost, "/illusion/event-manage-templates.jsf", "manage-templates");
    addDomainRule(configuration, siteHost, "/illusion/event-groups.jsf", "groups");
    addDomainRule(configuration, siteHost, "/illusion/event-materials.jsf", "materials");
    addDomainRule(configuration, siteHost, "/illusion/event-forum.jsf", "event-forum");
    addDomainRule(configuration, siteHost, "/illusion/event-participants.jsf", "participants");
    addDomainRule(configuration, siteHost, "/illusion/event-payment.jsf", "payment");
    addDomainRule(configuration, siteHost, "/illusion/event-settings.jsf", "settings");
    addDomainRule(configuration, siteHost, "/illusion/dojoin.jsf", "dojoin");
    addDomainRule(configuration, siteHost, "/illusion/event-edit-page.jsf", "edit-page", null, new String[] { "pageId" });
    addDomainRule(configuration, siteHost, "/illusion/event-edit-template.jsf", "edit-template", null, new String[] { "templateId" });
    addDomainRule(configuration, siteHost, "/illusion/event-material.jsf", "materials/{materialPath}", new String[] { "materialPath" }, null);
    addDomainRule(configuration, siteHost, "/illusion/event-page.jsf", "pages/{materialPath}", new String[] { "materialPath" }, null);
    
    configuration.addRule()
      .when(Direction.isOutbound()
        .and(Path.matches("/illusion/event.jsf"))
        .and(Query.parameterExists("urlName"))
        .and(new EventUrlNameRule("urlName"))
      )
      .perform(Substitute.with(String.format("http://{eventDomain}%s%s", port, contextPath))
        .and(Log.message(Level.DEBUG, String.format("Event to custom domain outbound substitute %s", String.format("http://{eventDomain}%s%s", port, contextPath))))    
      );
    
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
    
  private void addDomainRule(ConfigurationBuilder configuration, String siteHost, String jsfRule, String page) {
    addDomainRule(configuration, siteHost, jsfRule, page, null, null);
  }
  
  private void addDomainRule(ConfigurationBuilder configuration, String siteHost, String jsfRule, String page, String[] pathParams, String[] queryParams) {
    String path = "/" + page;
    addInboundDomainRule(configuration, siteHost, page, path);
    addOutboundDomainRule(configuration, siteHost, jsfRule, path, pathParams, queryParams);
  }

  private void addInboundDomainRule(ConfigurationBuilder configuration, String siteHost, String page, String path) {
    String substitute = "/illusion/event/{eventUrlName}" + (page.equals("") ? "" : "/" + page);
    configuration.addRule()
      .when(
          Direction.isInbound()
            .andNot(Domain.matches(siteHost))
            .and(Path.matches(path))
            .and(new EventCustomDomainRule())
      )
      .perform(Substitute.with(substitute).and(Log.message(Level.DEBUG, String.format("Custom domain inbound substitute %s -> %s", path, substitute))));
  }

  private void addOutboundDomainRule(ConfigurationBuilder configuration, String siteHost, String jsfRule, String path, String[] pathParams, String[] queryParams) {
    ConditionBuilder when = Direction.isOutbound()
      .andNot(Domain.matches(siteHost))
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

    when = when.and(new EventCustomDomainRule());
    
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

  private synchronized String getDomainForUrlName(String urlName) {
    String domain = eventUrlNameCustomDomainMap.get(urlName);
    if ("N/A".equals(domain)) {
      return null;
    }
    
    if (StringUtils.isBlank(domain)) {
      IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(urlName);
      if (illusionEvent != null) {
        domain = illusionEvent.getDomain();
        
        if (StringUtils.isNotBlank(domain)) {
          eventUrlNameCustomDomainMap.put(urlName, domain);
          eventCustomDomainUrlNameMap.put(domain, urlName);
          return domain;
        } else {
          eventUrlNameCustomDomainMap.put(urlName, "N/A");
          return null;
        }
      } else {
        eventUrlNameCustomDomainMap.put(urlName, "N/A");
      }
    }
    
    return domain;
  }
  
  private synchronized String getUrlNameByDomain(String domain) {
    String urlName = eventCustomDomainUrlNameMap.get(domain);
    if ("N/A".equals(urlName)) {
      return null;
    }
    
    if (StringUtils.isBlank(urlName)) {
      IllusionEvent illusionEvent = illusionEventController.findIllusionEventByDomain(domain);
      if (illusionEvent != null) {
        String eventUrl = illusionEvent.getUrlName();
        
        if (StringUtils.isNotBlank(eventUrl)) {
          eventUrlNameCustomDomainMap.put(eventUrl, domain);
          eventCustomDomainUrlNameMap.put(domain, eventUrl);
          return domain;
        } else {
          eventCustomDomainUrlNameMap.put(domain, "N/A");
          return null;
        }
      } else {
        eventCustomDomainUrlNameMap.put(domain, "N/A");
      }
    }
    
    return urlName;
  }

  private Map<String, String> eventCustomDomainUrlNameMap;
  private Map<String, String> eventUrlNameCustomDomainMap;

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
        String urlName = getUrlNameByDomain(hostName);
        if (StringUtils.isNotBlank(urlName)) {
          ParameterValueStore parameterValueStore = (ParameterValueStore) context.get(ParameterValueStore.class);
          parameterValueStore.submit(event, context, parameterStore.get("eventUrlName"), urlName);
          return true;
        }
      }
      
      return false;
    }
    
    private ParameterStore parameterStore;
  }
  
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
        String eventDomain = getDomainForUrlName(urlName);
        if (StringUtils.isNotBlank(eventDomain)) {
          parameterValueStore.submit(event, context, parameterStore.get("eventDomain"), eventDomain);
          return true;
        }
      }
      
      return false;
    }

    private ParameterStore parameterStore;
    private String paramName;
  }
 
}
