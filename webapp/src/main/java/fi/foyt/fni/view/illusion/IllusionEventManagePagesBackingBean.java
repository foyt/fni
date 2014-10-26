package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.materials.IllusionEventDocumentController;
import fi.foyt.fni.materials.MaterialController;
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
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private IllusionEventDocumentController illusionEventDocumentController;

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
    
    pages = illusionEventPageController.listPages(illusionEvent);
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public List<IllusionEventPage> getPages() {
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
    String pageUrlName = materialController.getUniqueMaterialUrlName(sessionController.getLoggedUser(), event.getFolder(), null, title);
    Language language = systemSettingsController.findLocaleByIso2(sessionController.getLocale().getLanguage());
    IllusionEventDocument page = illusionEventDocumentController.createIllusionEventDocument(sessionController.getLoggedUser(), IllusionEventDocumentType.PAGE, language, event.getFolder(), pageUrlName, title, "", MaterialPublicity.PUBLIC);
    return "/illusion/event-edit-page.jsf?faces-redirect=true&urlName=" + event.getUrlName() + "&id=" + page.getId();
  }
  
  public String changePageVisibility() {
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
    illusionEventPageController.setPageVisibility(event, getPageId(), getPageVisibility());
    return "/illusion/event-manage-pages.jsf?faces-redirect=true&urlName=" + event.getUrlName();
  }

  private List<IllusionEventPage> pages;
  private String pageId;
  private IllusionEventPageVisibility pageVisibility;
  
}
