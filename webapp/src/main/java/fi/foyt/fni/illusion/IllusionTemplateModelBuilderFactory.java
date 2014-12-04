package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.system.SystemSettingsController;

public class IllusionTemplateModelBuilderFactory {

  @Inject
  private SystemSettingsController systemSettingsController;
  
  public IllusionTemplateModelBuilder newBuilder() {
    return new IllusionTemplateModelBuilder();
  }

  public class IllusionTemplateModelBuilder {

    public IllusionTemplateModelBuilder() {
      model = new HashMap<>();
      pages = new ArrayList<>();
      adminPages = new ArrayList<>();
      localeKeys = new ArrayList<>();
      breadcrumps = new ArrayList<>();
    }

    public IllusionTemplateModelBuilder addPage(IllusionEventPage page) {
      pages.add(page);
      return this;
    }

    public IllusionTemplateModelBuilder addAdminPage(IllusionEventPage.Static id, String path, String title) {
      adminPages.add(new AdminPage(id.toString(), path, title));
      return this;
    }
    
    public IllusionTemplateModelBuilder addBreadcrump(IllusionEvent illusionEvent, String path, String text) {
      String eventUrl = getEventUrl(illusionEvent);
      breadcrumps.add(new Breadcrump(BreadcrumpType.EVENT, eventUrl + path, text));
      return this;
    }
    
    public IllusionTemplateModelBuilder defaults(Locale locale) {
      String siteUrl = systemSettingsController.getSiteUrl(false, true);
      model.put("siteUrl", siteUrl);
      model.put("contextPath", systemSettingsController.getSiteContextPath());
      
      breadcrumps.add(new Breadcrump(BreadcrumpType.ENVIRONMENT, siteUrl, ExternalLocales.getText(locale, "illusion.breadcrumps.forgeAndIllusion")));
      breadcrumps.add(new Breadcrump(BreadcrumpType.ENVIRONMENT, siteUrl + "/illusion", ExternalLocales.getText(locale, "illusion.breadcrumps.events")));
      
      return this;
    }

    public IllusionTemplateModelBuilder eventDefaults(IllusionEvent illusionEvent) {
      String eventUrl = getEventUrl(illusionEvent);

      model.put("eventUrl", eventUrl);
      model.put("eventName", illusionEvent.getName());
      model.put("eventDescription", illusionEvent.getDescription());

      breadcrumps.add(new Breadcrump(BreadcrumpType.EVENT, eventUrl, illusionEvent.getName()));
      
      return this;
    }

    private String getEventUrl(IllusionEvent illusionEvent) {
      String eventUrl;
      if (StringUtils.isNotBlank(illusionEvent.getDomain())) {
        eventUrl = systemSettingsController.getHostUrl(illusionEvent.getDomain(), false, true);
      } else {
        eventUrl = systemSettingsController.getSiteUrl(false, true) + "/illusion/event/" + illusionEvent.getUrlName();
      }
      
      return eventUrl;
    }
    
    public IllusionTemplateModelBuilder setCurrentPage(fi.foyt.fni.illusion.IllusionEventPage.Static selectedPage) {
      return setCurrentPage(selectedPage.toString());
    }

    public IllusionTemplateModelBuilder setCurrentPage(String currentPageId) {
      model.put("currentPageId", currentPageId);
      return this;
    }

    public IllusionTemplateModelBuilder addLocale(String key) {
      localeKeys.add(key);
      return this;
    }

    public IllusionTemplateModelBuilder addLocales(String... keys) {
      for (String key : keys) {
        localeKeys.add(key);
      }
      
      return this;
    }
    
    public IllusionTemplateModelBuilder put(String key, Object value) {
      model.put(key, value);
      return this;
    }

    public Map<String, Object> build(Locale locale) {
      Map<String, Object> result = new HashMap<>(model);
      Map<String, String> locales = new HashMap<>();
      
      for (String localeKey : localeKeys) {
        locales.put(localeKey, ExternalLocales.getText(locale, localeKey));
      }

      result.put("breadcrumps", breadcrumps);
      result.put("pages", pages);
      result.put("adminPages", adminPages);
      result.put("locales", locales);
      
      return result;
    }

    private Map<String, Object> model;
    private List<IllusionEventPage> pages;
    private List<AdminPage> adminPages;
    private List<String> localeKeys; 
    private List<Breadcrump> breadcrumps;
  }

  public class AdminPage {

    public AdminPage(String id, String path, String title) {
      this.id = id;
      this.path = path;
      this.title = title;
    }
    
    public String getId() {
      return id;
    }

    public String getTitle() {
      return title;
    }
    
    public String getPath() {
      return path;
    }

    private String id;
    private String path;
    private String title;
  }

  public class Breadcrump {

    public Breadcrump(BreadcrumpType type, String path, String text) {
      super();
      this.type = type;
      this.path = path;
      this.text = text;
    }

    public String getPath() {
      return path;
    }

    public String getText() {
      return text;
    }

    public BreadcrumpType getType() {
      return type;
    }

    private BreadcrumpType type;
    private String path;
    private String text;
  }

  private enum BreadcrumpType {

    ENVIRONMENT,

    EVENT

  }
}
