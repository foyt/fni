package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.EnumUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventPage.Static;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventSettingDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventDocumentDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Dependent
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

  /* Pages */

  public IllusionEventDocument findCustomPageById(Long id) {
    return illusionEventDocumentDAO.findById(id);
  }

  public List<IllusionEventDocument> listCustomPages(IllusionEventFolder folder) {
    return illusionEventDocumentDAO.listByParentFolderAndDocumentType(folder, IllusionEventDocumentType.PAGE);
  }

  public boolean isPageVisible(IllusionEventParticipant participant, IllusionEvent event, String pageId) {
    PageSetting pageSettings = getPageSettings(event, pageId);
    IllusionEventParticipantRole role = participant != null ? participant.getRole() : null;
    User user = participant == null ? null : participant.getUser();
    IllusionEventPageVisibility visibility = pageSettings.getVisibility();
    
    switch (visibility) {
      case HIDDEN:
        return false;
      case ORGANIZERS:
        return role == IllusionEventParticipantRole.ORGANIZER;
      case PARTICIPANTS:
        return 
          role == IllusionEventParticipantRole.PARTICIPANT || 
          role == IllusionEventParticipantRole.ORGANIZER;
      case VISIBLE:
        return true;
      case GROUPS:
        if (user != null) {
          if (role == IllusionEventParticipantRole.ORGANIZER) {
            return true;
          }
          
          for (Long groupId : pageSettings.getGroupIds()) {
            IllusionEventGroup group = illusionEventController.findGroupById(groupId);
            if (illusionEventController.findGroupMemberByGroupAndUser(group, user) != null) {
              return true;
            }
          }
        }
      break;
      default:
        logger.severe(String.format("Unknown visibility %s", visibility.toString()));
      break;
    }

    return false;
  }
  
  public boolean isPageVisible(IllusionEventParticipant participant, IllusionEvent event, IllusionEventPage.Static page) {
    return isPageVisible(participant, event, page.toString());
  }

  private boolean isPageVisible(IllusionEventParticipant participant, IllusionEvent event, IllusionEventPage page) {
    return isPageVisible(participant, event, page.getId());
  }
  
  private PageSetting getDefaultSettings(String pageId) {
    IllusionEventPageVisibility visibility;

    Static staticPageId = EnumUtils.getEnum(IllusionEventPage.Static.class, pageId);
    if (staticPageId != null) {
      switch (staticPageId) {
      case INDEX:
        visibility = IllusionEventPageVisibility.VISIBLE;
        break;
      case MANAGE_PAGES:
      case MANAGE_TEMPLATES:
      case SETTINGS:
      case PARTICIPANTS:
        visibility = IllusionEventPageVisibility.ORGANIZERS;
        break;
      default:
        visibility = IllusionEventPageVisibility.HIDDEN;
        break;
      }
    } else {
      visibility = IllusionEventPageVisibility.HIDDEN;
    }

    PageSetting pageSetting = new PageSetting();
    pageSetting.setVisibility(visibility);

    return pageSetting;
  }

  public List<IllusionEventPage> listVisiblePages(final IllusionEvent event,
      final IllusionEventParticipant participant) {
    List<IllusionEventPage> result = new ArrayList<>();

    for (IllusionEventPage page : listAllPages(event)) {
      if (isPageVisible(participant, event, page)) {
        result.add(page);
      }
    }

    return result;
  }

  public List<IllusionEventPage> listVisiblePages(IllusionEvent event, User user) {
    IllusionEventParticipant participant = user != null
        ? illusionEventController.findIllusionEventParticipantByEventAndUser(event, user) : null;
    return listVisiblePages(event, participant);
  }

  private List<IllusionEventPage> listAllPages(IllusionEvent illusionEvent) {
    List<IllusionEventPage> pages = new ArrayList<>();
    String eventUrl = illusionEventController.getEventUrl(illusionEvent);

    IllusionEventDocument indexDocument = illusionEventDocumentDAO
        .findByParentFolderAndDocumentType(illusionEvent.getFolder(), IllusionEventDocumentType.INDEX);
    if (indexDocument != null) {
      pages.add(new IllusionEventPage("INDEX", indexDocument.getUrlName(), eventUrl, indexDocument.getTitle(),
          IllusionEventPage.Static.INDEX.toString()));
    } else {
      logger.severe("Could not find index page document for event #" + illusionEvent.getId());
    }

    for (IllusionEventDocument customPage : listCustomPages(illusionEvent.getFolder())) {
      pages.add(new IllusionEventPage(customPage.getId().toString(), customPage.getUrlName(),
          eventUrl + "/pages/" + customPage.getUrlName(), customPage.getTitle(), "PAGE"));
    }

    pages.add(new IllusionEventPage(IllusionEventPage.Static.MATERIALS.toString(), "materials", eventUrl + "/materials",
        ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.materials"), "MATERIALS"));
    pages.add(new IllusionEventPage(IllusionEventPage.Static.FORUM.toString(), "event-forum", eventUrl + "/event-forum",
        ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.forum"), "FORUM"));

    return pages;
  }

  public PageSettings getPageSettings(IllusionEvent illusionEvent) {
    if (eventPageSettings.containsKey(illusionEvent.getId())) {
      return eventPageSettings.get(illusionEvent.getId());
    }

    PageSettings pageSettings = null;

    ObjectMapper objectMapper = new ObjectMapper();

    IllusionEventSetting setting = illusionEventSettingDAO.findByEventAndKey(illusionEvent,
        IllusionEventSettingKey.PAGE_SETTINGS);
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

  public PageSetting getPageSettings(IllusionEvent illusionEvent, String pageId) {
    PageSettings pageSettings = getPageSettings(illusionEvent);
    if (!pageSettings.containsKey(pageId)) {
      return getDefaultSettings(pageId);
    }

    return pageSettings.get(pageId);
  }

  public void updatePageSetting(IllusionEvent illusionEvent, String pageId, PageSetting pageSetting) {
    PageSettings pageSettings = getPageSettings(illusionEvent);
    pageSettings.put(pageId, pageSetting);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String settingValue = objectMapper.writeValueAsString(pageSettings);
      IllusionEventSetting setting = illusionEventSettingDAO.findByEventAndKey(illusionEvent,
          IllusionEventSettingKey.PAGE_SETTINGS);
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

    public List<Long> getGroupIds() {
      return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
      this.groupIds = groupIds;
    }

    private IllusionEventPageVisibility visibility;

    @JsonInclude(Include.NON_NULL)
    private List<Long> groupIds;
  }

}
