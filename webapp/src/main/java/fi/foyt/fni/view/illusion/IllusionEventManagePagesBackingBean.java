package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionEventPageController.PageSetting;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.materials.IllusionEventDocumentDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/manage-pages", to = "/illusion/event-manage-pages.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext(context = "@urlName")
public class IllusionEventManagePagesBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventDocumentDAO illusionEventDocumentDAO;

  @Inject
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private MaterialController materialController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant member) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MANAGE_PAGES);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    pages = loadPages(illusionEvent);
    
    return null;
  }

  private List<Page> loadPages(IllusionEvent event) {
    List<Page> pages = new ArrayList<>();
    String eventUrl = illusionEventController.getEventUrl(event);
    
    IllusionEventDocument indexDocument = illusionEventDocumentDAO.findByParentFolderAndDocumentType(event.getFolder(), IllusionEventDocumentType.INDEX);
    if (indexDocument != null) {
      pages.add(createPage(event, "INDEX", indexDocument.getUrlName(), eventUrl, indexDocument.getTitle(), "INDEX", true, false, false, false));
    } else {
      logger.severe("Could not find index page document for event #" + event.getId());
    }
    
    for (IllusionEventDocument customPage : illusionEventPageController.listCustomPages(event.getFolder())) {
      pages.add(createPage(event, customPage.getId().toString(), customPage.getUrlName(), eventUrl + "/pages/" + customPage.getUrlName(), customPage.getTitle(), "PAGE", true, true, true, false));
    }
    
    pages.add(createPage(event, "MATERIALS", "materials", eventUrl + "/materials", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.materials"), "MATERIALS", false, false, true, true));
    pages.add(createPage(event, "FORUM", "event-forum", eventUrl + "/event-forum", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.forum"), "FORUM", false, false, true, false));

    return pages;
  }
  
  private Page createPage(IllusionEvent event, String id, String urlName, String url, String title, String type, boolean editable, boolean deletable,
      boolean visibilityChangeable, boolean requiresUser) {
    
    PageSetting setting = getPageSettings(event, id); 
    IllusionEventPageVisibility visibility = setting.getVisibility();
    String groupIds = setting.getGroupIds() != null ? StringUtils.join(setting.getGroupIds().toArray(new Long[0]), ",") : null;
    
    return new Page(id, urlName, url, title, type, editable, deletable, visibilityChangeable, requiresUser, visibility, groupIds);
  }
  
  private PageSetting getPageSettings(IllusionEvent illusionEvent, String pageId) {
    return illusionEventPageController.getPageSettings(illusionEvent, pageId);
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public List<Page> getPages() {
    return pages;
  }

  public String getPageId() {
    return pageId;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public IllusionEventPageVisibility getPageVisibility() {
    return pageVisibility;
  }
  
  public void setPageVisibility(IllusionEventPageVisibility pageVisibility) {
    this.pageVisibility = pageVisibility;
  }
  
  public List<Long> getGroupIds() {
    return groupIds;
  }
  
  public void setGroupIds(List<Long> groupIds) {
    this.groupIds = groupIds;
  }

  public String getRelativePath(Material material) {
    List<String> path = new ArrayList<>();

    Material current = material;
    do {
      path.add(0, current.getUrlName());
    } while ((current == null) || (current.getType() == MaterialType.ILLUSION_FOLDER));

    return StringUtils.join(path, "/");
  }

  public String newPage() {
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
    String title = FacesUtils.getLocalizedValue("illusion.managePages.untitledPage");
    String pageUrlName = materialController.getUniqueMaterialUrlName(sessionController.getLoggedUser(),
        event.getFolder(), null, title);
    Language language = systemSettingsController.findLocaleByIso2(sessionController.getLocale().getLanguage());
    IllusionEventDocument page = illusionEventController.createIllusionEventDocument(sessionController.getLoggedUser(),
        IllusionEventDocumentType.PAGE, language, event.getFolder(), pageUrlName, title, "", MaterialPublicity.PUBLIC);
    return "/illusion/event-edit-page.jsf?faces-redirect=true&urlName=" + event.getUrlName() + "&pageId="
        + page.getId();
  }

  public String changePageVisibility() {
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
    
    IllusionEventPageVisibility visibility = getPageVisibility();
    List<Long> groupIds = visibility == IllusionEventPageVisibility.GROUPS ? getGroupIds() : null;
    
    PageSetting pageSettings = illusionEventPageController.getPageSettings(event, getPageId());
    pageSettings.setGroupIds(groupIds);
    pageSettings.setVisibility(visibility);
    
    illusionEventPageController.updatePageSetting(event, getPageId(), pageSettings);

    return "/illusion/event-manage-pages.jsf?faces-redirect=true&urlName=" + event.getUrlName();
  }

  private List<Page> pages;
  private String pageId;
  private IllusionEventPageVisibility pageVisibility;
  private List<Long> groupIds;
  
  public class Page {

    public Page(String id, String urlName, String url, String title, String type, boolean editable, boolean deletable,
        boolean visibilityChangeable, boolean requiresUser, IllusionEventPageVisibility visibility, String groupIds) {
      super();
      this.id = id;
      this.urlName = urlName;
      this.url = url;
      this.title = title;
      this.type = type;
      this.editable = editable;
      this.deletable = deletable;
      this.visibilityChangeable = visibilityChangeable;
      this.requiresUser = requiresUser;
      this.visibility = visibility;
      this.groupIds = groupIds;
    }

    public String getId() {
      return id;
    }

    public String getUrlName() {
      return urlName;
    }

    public String getUrl() {
      return url;
    }

    public String getTitle() {
      return title;
    }

    public String getType() {
      return type;
    }

    public boolean isEditable() {
      return editable;
    }

    public boolean isDeletable() {
      return deletable;
    }

    public boolean isVisibilityChangeable() {
      return visibilityChangeable;
    }

    public boolean isRequiresUser() {
      return requiresUser;
    }

    public IllusionEventPageVisibility getVisibility() {
      return visibility;
    }

    public String getGroupIds() {
      return groupIds;
    }

    private String id;
    private String urlName;
    private String url;
    private String title;
    private String type;
    private boolean editable;
    private boolean deletable;
    private boolean visibilityChangeable;
    private boolean requiresUser;
    private IllusionEventPageVisibility visibility;
    private String groupIds;
  }
}
