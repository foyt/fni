package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.i18n.ExternalLocales;
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
      locales = new HashMap<>();
      breadcrumps = new ArrayList<>();
    }

    public IllusionTemplateModelBuilder addPage(IllusionEventPage page) {
      pages.add(page);
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

    public IllusionTemplateModelBuilder addLocale(Locale locale, String key) {
      locales.put(key, ExternalLocales.getText(locale, key));
      return this;
    }
    
    public IllusionTemplateModelBuilder put(String key, Object value) {
      model.put(key, value);
      return this;
    }

    public Map<String, Object> build() {
      Map<String, Object> result = new HashMap<>(model);

      result.put("breadcrumps", breadcrumps);
      result.put("pages", pages);
      result.put("locales", locales);
      
      return result;
    }

    private Map<String, Object> model;
    private List<IllusionEventPage> pages;
    private Map<String, String> locales; 
    private List<Breadcrump> breadcrumps;
  }

  public class Page {

    public Page(String urlName, String title, String content) {
      super();
      this.urlName = urlName;
      this.title = title;
      this.content = content;
    }

    public String getContent() {
      return content;
    }

    public String getTitle() {
      return title;
    }

    public String getUrlName() {
      return urlName;
    }

    private String urlName;
    private String title;
    private String content;
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
