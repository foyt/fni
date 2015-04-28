package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.larpkalenteri.LarpKalenteriEventMissingException;
import fi.foyt.fni.larpkalenteri.UnsupportedTypeException;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGenre;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/settings", to = "/illusion/event-settings.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext(context = "@urlName")
public class IllusionEventSettingsBackingBean extends AbstractIllusionEventBackingBean {
  
  @Parameter
  private String urlName;

  @Inject
  private Logger logger;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;
  
  @Inject
  private NavigationController navigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      return navigationController.accessDenied();
    }

    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.SETTINGS);
    illusionEventNavigationController.setEventUrlName(getUrlName());

    published = illusionEvent.getPublished();
    name = illusionEvent.getName();
    description = illusionEvent.getDescription();
    location = illusionEvent.getLocation();
    joinMode = illusionEvent.getJoinMode();
    start = formatDateTime(illusionEvent.getStart());
    end = formatDateTime(illusionEvent.getEnd());
    domain = illusionEvent.getDomain();
    ageLimit = illusionEvent.getAgeLimit();
    imageUrl = illusionEvent.getImageUrl();
    beginnerFriendly = illusionEvent.getBeginnerFriendly();
    signUpStartDate = formatDate(illusionEvent.getSignUpStartDate());
    signUpEndDate = formatDate(illusionEvent.getSignUpEndDate());
    typeId = illusionEvent.getType() != null ? illusionEvent.getType().getId() : null;
    genreIds = new ArrayList<Long>();
    signUpFeeCurrency = illusionEvent.getSignUpFeeCurrency() != null ? illusionEvent.getSignUpFeeCurrency().getCurrencyCode() : null;
    signUpFeeText = illusionEvent.getSignUpFeeText();
    larpKalenteriSync = StringUtils.isNotBlank(illusionEventController.getSetting(illusionEvent, IllusionEventSettingKey.LARP_KALENTERI_ID));

    List<IllusionEventGenre> eventGenres = illusionEventController.listIllusionEventGenres(illusionEvent);
    for (IllusionEventGenre eventGenre : eventGenres) {
      genreIds.add(eventGenre.getGenre().getId());
    }
    
    List<IllusionEventType> eventTypes = illusionEventController.listTypes();
    typeSelectItems = new ArrayList<>(eventTypes.size());
    for (IllusionEventType eventType : eventTypes) {
      typeSelectItems.add(new SelectItem(eventType.getId(), eventType.getName()));
    }

    genres = illusionEventController.listGenres();
    
    return null;
  }
  
  public Boolean getPublished() {
    return published;
  }
  
  public void setPublished(Boolean published) {
    this.published = published;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
  
  public Double getLocationLat() {
    return locationLat;
  }
  
  public void setLocationLat(Double locationLat) {
    this.locationLat = locationLat;
  }
  
  public Double getLocationLon() {
    return locationLon;
  }
  
  public void setLocationLon(Double locationLon) {
    this.locationLon = locationLon;
  }

  public IllusionEventJoinMode getJoinMode() {
    return joinMode;
  }

  public void setJoinMode(IllusionEventJoinMode joinMode) {
    this.joinMode = joinMode;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public String getStart() {
    return start;
  }
  
  public void setStart(String start) {
    this.start = start;
  }
  
  public String getEnd() {
    return end;
  }
  
  public void setEnd(String end) {
    this.end = end;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public Integer getAgeLimit() {
    return ageLimit;
  }

  public void setAgeLimit(Integer ageLimit) {
    this.ageLimit = ageLimit;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Boolean getBeginnerFriendly() {
    return beginnerFriendly;
  }

  public void setBeginnerFriendly(Boolean beginnerFriendly) {
    this.beginnerFriendly = beginnerFriendly;
  }
  
  public Boolean getLarpKalenteriSync() {
    return larpKalenteriSync;
  }
  
  public void setLarpKalenteriSync(Boolean larpKalenteriSync) {
    this.larpKalenteriSync = larpKalenteriSync;
  }
  
  public String getSignUpStartDate() {
    return signUpStartDate;
  }
  
  public void setSignUpStartDate(String signUpStartDate) {
    this.signUpStartDate = signUpStartDate;
  }
  
  public String getSignUpEndDate() {
    return signUpEndDate;
  }
  
  public void setSignUpEndDate(String signUpEndDate) {
    this.signUpEndDate = signUpEndDate;
  }

  public Long getTypeId() {
    return typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  public List<Long> getGenreIds() {
    return genreIds;
  }

  public void setGenreIds(List<Long> genreIds) {
    this.genreIds = genreIds;
  }

  public List<Genre> getGenres() {
    return genres;
  }

  public List<SelectItem> getTypeSelectItems() {
    return typeSelectItems;
  }
  
  public String getSignUpFeeCurrency() {
    return signUpFeeCurrency;
  }
  
  public void setSignUpFeeCurrency(String signUpFeeCurrency) {
    this.signUpFeeCurrency = signUpFeeCurrency;
  }
  
  public String getSignUpFeeText() {
    return signUpFeeText;
  }
  
  public void setSignUpFeeText(String signUpFeeText) {
    this.signUpFeeText = signUpFeeText;
  }

  public String save() throws Exception {
    IllusionEventType type = illusionEventController.findTypeById(getTypeId());
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(getUrlName());
    
    String signUpFeeText = getSignUpFeeText();
    Currency signUpFeeCurrency = null;

    if (StringUtils.isNotBlank(signUpFeeText)) {
      signUpFeeCurrency = Currency.getInstance(getSignUpFeeCurrency());
    }
    
    illusionEventController.updateIllusionEventName(illusionEvent, getName());
    illusionEventController.updateIllusionEventDescription(illusionEvent, getDescription());
    illusionEventController.updateIllusionEventJoinMode(illusionEvent, getJoinMode());
    illusionEventController.updateIllusionEventStart(illusionEvent, parseDate(getStart()));
    illusionEventController.updateIllusionEventEnd(illusionEvent, parseDate(getEnd()));
    illusionEventController.updateIllusionEventLocation(illusionEvent, getLocation());
    illusionEventController.updateIllusionEventType(illusionEvent, type);
    illusionEventController.updateIllusionEventSignUpTimes(illusionEvent, parseDate(getSignUpStartDate()), parseDate(getSignUpEndDate()));
    illusionEventController.updateIllusionEventAgeLimit(illusionEvent, getAgeLimit());
    illusionEventController.updateIllusionEventBeginnerFriendly(illusionEvent, getBeginnerFriendly());
    illusionEventController.updateIllusionEventImageUrl(illusionEvent, getImageUrl());
    illusionEventController.updateEventSignUpFee(illusionEvent, signUpFeeText, illusionEvent.getSignUpFee(), signUpFeeCurrency);
    
    if (StringUtils.isNotBlank(getDomain()) && !illusionEventController.isEventAllowedDomain(getDomain())) {
      FacesUtils.addMessage(javax.faces.application.FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.eventSettings.customDomainInvalid"));
      return null;
    } else {
      illusionEventController.updateEventDomain(illusionEvent, getDomain());
    }
    
    List<Genre> genres = new ArrayList<>();
    
    for (Long genreId : genreIds) {
      Genre genre = illusionEventController.findGenreById(genreId);
      genres.add(genre);
    }
    
    illusionEventController.updateEventGenres(illusionEvent, genres);
    illusionEventController.updatePublished(illusionEvent, getPublished());
    
    if (getLarpKalenteriSync()) {
      try {
        illusionEventController.updateLarpKalenteriEvent(illusionEvent, getLocationLat(), getLocationLon());
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to synchronize event into Larp-kalenteri", e);
      } catch (LarpKalenteriEventMissingException e) {
        FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.eventSettings.eventRemovedFromLarpKalenteri", type.getName()));
      } catch (UnsupportedTypeException e) {
        FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.eventSettings.eventTypeNotSynchronizableToLarpKalenteri", type.getName()));
      }
    } else {
      illusionEventController.setSetting(illusionEvent, IllusionEventSettingKey.LARP_KALENTERI_ID, null);
    }
    
    return "/illusion/event-settings.jsf?faces-redirect=true&urlName=" + illusionEvent.getUrlName();
  }

  private String formatDate(Date time) {
    if (time == null) {
      return null;
    }

    DateTimeFormatter formatter = ISODateTimeFormat.date();
    return formatter.print(time.getTime());
  }

  private String formatDateTime(Date time) {
    if (time == null) {
      return null;
    }

    DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
    return formatter.print(time.getTime());
  }

  private Date parseDate(String iso) {
    if (StringUtils.isBlank(iso)) {
      return null;
    }

    DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
    return parser.parseDateTime(iso).toDate();
  }

  private Boolean published;
  private String name;
  private String description;
  private String location;
  private Double locationLat;
  private Double locationLon;
  private IllusionEventJoinMode joinMode;
  private String start;
  private String end;
  private String domain;
  private Integer ageLimit;
  private String imageUrl;
  private Boolean beginnerFriendly;
  private Boolean larpKalenteriSync;
  private String signUpStartDate;
  private String signUpEndDate;
  private List<Genre> genres;
  private Long typeId;
  private String signUpFeeCurrency;
  private String signUpFeeText;
  private List<Long> genreIds;
  private List<SelectItem> typeSelectItems;
}
