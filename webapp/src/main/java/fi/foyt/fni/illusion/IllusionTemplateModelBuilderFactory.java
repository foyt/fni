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
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

public class IllusionTemplateModelBuilderFactory {

  @Inject
  private UserController userController;
  
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
      breadcrumbs = new ArrayList<>();
    }

    public IllusionTemplateModelBuilder addPage(IllusionEventPage page) {
      pages.add(page);
      return this;
    }

    public IllusionTemplateModelBuilder addAdminPage(IllusionEventPage.Static id, String path, String title) {
      adminPages.add(new AdminPage(id.toString(), path, title));
      return this;
    }

    public IllusionTemplateModelBuilder addBreadcrumb(IllusionEvent illusionEvent, String path, String text) {
      String eventUrl = getEventUrl(illusionEvent);
      breadcrumbs.add(new Breadcrumb(BreadcrumbType.EVENT, eventUrl + path, text));
      return this;
    }

    public IllusionTemplateModelBuilder defaults(Locale locale) {
      String siteUrl = systemSettingsController.getSiteUrl(false, true);
      model.put("siteUrl", siteUrl);
      model.put("contextPath", systemSettingsController.getSiteContextPath());

      breadcrumbs.add(new Breadcrumb(BreadcrumbType.ENVIRONMENT, siteUrl, ExternalLocales.getText(locale, "illusion.breadcrumbs.forgeAndIllusion")));
      breadcrumbs.add(new Breadcrumb(BreadcrumbType.ENVIRONMENT, siteUrl + "/illusion", ExternalLocales.getText(locale, "illusion.breadcrumbs.events")));

      return this;
    }

    public IllusionTemplateModelBuilder eventDefaults(IllusionEvent illusionEvent) {
      String eventUrl = getEventUrl(illusionEvent);

      model.put("eventUrl", eventUrl);
      model.put("eventName", illusionEvent.getName());
      model.put("eventDescription", illusionEvent.getDescription());

      breadcrumbs.add(new Breadcrumb(BreadcrumbType.EVENT, eventUrl, illusionEvent.getName()));

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

    public IllusionTemplateModelBuilder setUserInfo(User user, IllusionEventParticipant participant) {
      Participant participantInfo = null;
      
      if (participant != null) {
        participantInfo = new Participant(participant.getId(), participant.getRole().toString());
      }
      
      put("user", new UserInfo(user.getId(), userController.getUserDisplayName(user), user.getFirstName(), user.getLastName(), participantInfo));
      
      return this;
    }

    public Map<String, Object> build(Locale locale) {
      Map<String, Object> result = new HashMap<>(model);
      Map<String, String> locales = new HashMap<>();

      for (String localeKey : localeKeys) {
        locales.put(localeKey, ExternalLocales.getText(locale, localeKey));
      }

      result.put("breadcrumbs", breadcrumbs);
      result.put("pages", pages);
      result.put("adminPages", adminPages);
      result.put("locales", locales);
      
      return result;
    }

    private Map<String, Object> model;
    private List<IllusionEventPage> pages;
    private List<AdminPage> adminPages;
    private List<String> localeKeys;
    private List<Breadcrumb> breadcrumbs;
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

  public class Breadcrumb {

    public Breadcrumb(BreadcrumbType type, String url, String text) {
      super();
      this.type = type;
      this.url = url;
      this.text = text;
    }

    public String getUrl() {
      return url;
    }
    
    public String getText() {
      return text;
    }

    public BreadcrumbType getType() {
      return type;
    }

    private BreadcrumbType type;
    private String url;
    private String text;
  }

  private enum BreadcrumbType {

    ENVIRONMENT,

    EVENT

  }

  @SuppressWarnings ("unused")
  private class UserInfo {

    public UserInfo(Long id, String displayName, String firstName, String lastName, Participant participant) {
      super();
      this.id = id;
      this.displayName = displayName;
      this.firstName = firstName;
      this.lastName = lastName;
      this.participant = participant;
    }

    public Long getId() {
      return id;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public Participant getParticipant() {
      return participant;
    }

    private Long id;
    private String displayName;
    private String firstName;
    private String lastName;
    private Participant participant;
  }

  @SuppressWarnings ("unused")
  private class Participant {

    public Participant(Long id, String role) {
      super();
      this.id = id;
      this.role = role;
    }

    public Long getId() {
      return id;
    }

    public String getRole() {
      return role;
    }

    private Long id;
    private String role;
  }
}
