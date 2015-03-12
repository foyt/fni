package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventSettingDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventDocumentDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.session.SessionController;

@Dependent
@Stateless
public class IllusionEventPageController {

  @Inject
  private Logger logger;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventDocumentDAO illusionEventDocumentDAO;

  @Inject
  private IllusionEventSettingDAO illusionEventSettingDAO;

  @Inject
  private SessionController sessionController;
  
  @PostConstruct
  public void init() {
    eventPageSettings = new HashMap<>();
  }

  /* Pages*/
  
  public IllusionEventDocument findCustomPageById(Long id) {
    return illusionEventDocumentDAO.findById(id);
  }

  public List<IllusionEventDocument> listCustomPages(IllusionEventFolder folder) {
    return illusionEventDocumentDAO.listByParentFolderAndDocumentType(folder, IllusionEventDocumentType.PAGE);
  }
  
  public List<IllusionEventPage> listPages(IllusionEvent illusionEvent) {
    List<IllusionEventPage> pages = new ArrayList<>();
    String eventUrl = illusionEventController.getEventUrl(illusionEvent);
    
    IllusionEventDocument indexDocument = illusionEventDocumentDAO.findByParentFolderAndDocumentType(illusionEvent.getFolder(), IllusionEventDocumentType.INDEX);
    if (indexDocument != null) {
      pages.add(new IllusionEventPage("INDEX", indexDocument.getUrlName(), eventUrl, indexDocument.getTitle(), "INDEX", true, false, false, false, getPageVisibility(illusionEvent, "INDEX")));
    } else {
      logger.severe("Could not find index page document for event #" + illusionEvent.getId());
    }
    
    for (IllusionEventDocument customPage : listCustomPages(illusionEvent.getFolder())) {
      pages.add(new IllusionEventPage(customPage.getId().toString(), customPage.getUrlName(), eventUrl + "/pages/" + customPage.getUrlName(), customPage.getTitle(), "PAGE", true, true, true, false, getPageVisibility(illusionEvent, customPage.getId().toString())));
    }
    
    pages.add(new IllusionEventPage("MATERIALS", "materials", eventUrl + "/materials", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.materials"), "MATERIALS", false, false, true, true, getPageVisibility(illusionEvent, "MATERIALS")));
    pages.add(new IllusionEventPage("FORUM", "event-forum", eventUrl + "/event-forum", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.forum"), "FORUM", false, false, true, false, getPageVisibility(illusionEvent, "FORUM")));

    return pages;
  }
  
  public List<IllusionEventPage> listParticipantPages(IllusionEvent illusionEvent) {
    List<IllusionEventPage> result = new ArrayList<>();
    for (IllusionEventPage page : listPages(illusionEvent)) {
      if ((page.getVisibility() == IllusionEventPageVisibility.VISIBLE) || (page.getVisibility() == IllusionEventPageVisibility.PARTICIPANTS)) {
        result.add(page);
      }
    }

    return result;
  }

  public List<IllusionEventPage> listPublicPages(IllusionEvent illusionEvent) {
    List<IllusionEventPage> result = new ArrayList<>();
    for (IllusionEventPage page : listPages(illusionEvent)) {
      if (page.getVisibility() == IllusionEventPageVisibility.VISIBLE) {
        result.add(page);
      }
    }

    return result;
  }
  
  public synchronized IllusionEventPageVisibility getPageVisibility(IllusionEvent illusionEvent, String pageId) {
    IllusionEventPageVisibility visibility = getPageSetting(illusionEvent, pageId).getVisibility();
    if (visibility != null) {
      return visibility;
    }
    
    switch (pageId) {
      case "INDEX":
        return IllusionEventPageVisibility.VISIBLE;
    }
    
    return IllusionEventPageVisibility.HIDDEN;
  }
  
  public synchronized void setPageVisibility(IllusionEvent illusionEvent, String pageId, IllusionEventPageVisibility visibility) {
    PageSetting pageSetting = getPageSetting(illusionEvent, pageId);
    pageSetting.setVisibility(visibility);
    updatePageSetting(illusionEvent, pageId, pageSetting);
  }

  private synchronized PageSettings getPageSettings(IllusionEvent illusionEvent) {
    if (eventPageSettings.containsKey(illusionEvent.getId())) {
      return eventPageSettings.get(illusionEvent.getId());
    }
    
    PageSettings pageSettings = null;
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    IllusionEventSetting setting = illusionEventSettingDAO.findByEventAndKey(illusionEvent, IllusionEventSettingKey.PAGE_SETTINGS);
    if (setting != null) {
      try {
        pageSettings = objectMapper.readValue(setting.getValue(), PageSettings.class);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Failed to unmarshal page settings", e);
      }
    }
    
    if (pageSettings == null) {
      pageSettings = new PageSettings();
    }
    
    eventPageSettings.put(illusionEvent.getId(), pageSettings);
    
    return pageSettings;
  }

  private synchronized PageSetting getPageSetting(IllusionEvent illusionEvent, String pageId) {
    PageSettings pageSettings = getPageSettings(illusionEvent);
    if (!pageSettings.containsKey(pageId)) {
      return new PageSetting();
    }
    
    return pageSettings.get(pageId);
  }
  
  private synchronized void updatePageSetting(IllusionEvent illusionEvent, String pageId, PageSetting pageSetting) {
    PageSettings pageSettings = getPageSettings(illusionEvent);
    pageSettings.put(pageId, pageSetting); 
    
    try {
      String settingValue = (new ObjectMapper()).writeValueAsString(pageSettings);      
      IllusionEventSetting setting = illusionEventSettingDAO.findByEventAndKey(illusionEvent, IllusionEventSettingKey.PAGE_SETTINGS);
      if (setting == null) {
        illusionEventSettingDAO.create(illusionEvent, IllusionEventSettingKey.PAGE_SETTINGS, settingValue);
      } else {
        illusionEventSettingDAO.updateValue(setting, settingValue);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to marshal page settings", e);
    }
    
    eventPageSettings.remove(illusionEvent.getId());
  }
  
  private Map<Long, PageSettings> eventPageSettings;
  
  public static class PageSettings extends HashMap<String, PageSetting> {

    private static final long serialVersionUID = 6527848805345493633L;
   
    public PageSettings() {
      super();
    }
    
  }
  
  public static class PageSetting {
    
    public PageSetting() {
      super();
    }
    
    public IllusionEventPageVisibility getVisibility() {
      return visibility;
    }
    
    public void setVisibility(IllusionEventPageVisibility visibility) {
      this.visibility = visibility;
    }
    
    private IllusionEventPageVisibility visibility;
  }
  
}
