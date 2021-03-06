package fi.foyt.fni.view.illusion;

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
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.larpkalenteri.UnsupportedTypeException;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.faces.FacesUtils;
import fi.foyt.fni.utils.time.DateTimeUtils;

@RequestScoped
@Stateful
@Named
@Join(path = "/illusion/createevent", to = "/illusion/createevent.jsf")
@LoggedIn
public class IllusionCreateEventBackingBean {
  
  @Inject
  private Logger logger;

  @Inject
  private SessionController sessionController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private NavigationController navigationController;

  @RequestAction
  public String init() {
    try {
      signUpFee = null;
      signUpFeeCurrency = systemSettingsController.getDefaultCurrency().getCurrencyCode();
      signUpFeeText = null;
  
      List<IllusionEventType> eventTypes = illusionEventController.listTypes();
      typeSelectItems = new ArrayList<>(eventTypes.size());
      for (IllusionEventType eventType : eventTypes) {
        typeSelectItems.add(new SelectItem(eventType.getId(), eventType.getName()));
      }
  
      genres = illusionEventController.listGenres();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unexpected error", e);
      return navigationController.internalError();
    }

    return null;
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

  public Double getSignUpFee() {
    return signUpFee;
  }

  public void setSignUpFee(Double signUpFee) {
    this.signUpFee = signUpFee;
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

  public List<Genre> getGenres() {
    return genres;
  }
  
  public List<SelectItem> getTypeSelectItems() {
    return typeSelectItems;
  }
  
  public List<Long> getGenreIds() {
    return genreIds;
  }
  
  public void setGenreIds(List<Long> genreIds) {
    this.genreIds = genreIds;
  }
  
  public String save() throws Exception {
    try {
      if (StringUtils.isBlank(getName())) {
        FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.createEvent.nameRequired"));
        return null;
      }
       
      Double signUpFee = getSignUpFee();
      String signUpFeeText = getSignUpFeeText();
      Currency signUpFeeCurrency = null;
  
      if (signUpFee != null && signUpFee <= 0) {
        signUpFee = null;
      }
  
      if ((signUpFee != null) || (StringUtils.isNotBlank(signUpFeeText))) {
        signUpFeeCurrency = Currency.getInstance(getSignUpFeeCurrency());
      }
      
      IllusionEventType type = illusionEventController.findTypeById(getTypeId());
      Date signUpStartDate = parseDate(getSignUpStartDate());
      Date signUpEndDate = parseDate(getSignUpEndDate());
      List<Genre> genres = new ArrayList<>();
      
      for (Long genreId : genreIds) {
        Genre genre = illusionEventController.findGenreById(genreId);
        genres.add(genre);
      }
      
      User loggedUser = sessionController.getLoggedUser();
      Date now = new Date();
      Date start = parseDateTime(getStart());
      Date end = parseDateTime(getEnd());
  
      if (start == null) {
        FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.createEvent.startRequired"));
        return null;
      }
  
      if (end == null) {
        FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.createEvent.endRequired"));
        return null;
      }
         
      IllusionEvent event = illusionEventController.createIllusionEvent(loggedUser, sessionController.getLocale(), getLocation(), getName(), 
          getDescription(), getJoinMode(), now, signUpFee, signUpFeeText, signUpFeeCurrency, start, end, 
          getAgeLimit(), getBeginnerFriendly(), getImageUrl(), type, signUpStartDate, signUpEndDate, genres);
  
      // Add organizer
      
      illusionEventController.createIllusionEventParticipant(loggedUser, event, IllusionEventParticipantRole.ORGANIZER);
      
      if (getLarpKalenteriSync()) {
        try {
          illusionEventController.updateLarpKalenteriEvent(event, getLocationLat(), getLocationLon());
        } catch (UnsupportedTypeException e) {
          FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.createEvent.eventTypeNotSynchronizableToLarpKalenteri", type.getName()));
        }
      }
      
      return "/illusion/event.jsf?faces-redirect=true&urlName=" + event.getUrlName();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unexpected error", e);
      throw e;
    }
  }

  private static Date parseDateTime(String iso) {
    if (StringUtils.isBlank(iso)) {
      return null;
    }
    
    return DateTimeUtils.toDate(DateTimeUtils.parseIsoOffsetDateTime(iso));
  }
  
  private static Date parseDate(String iso) {
    if (StringUtils.isBlank(iso)) {
      return null;
    }
    
    return DateTimeUtils.toDate(DateTimeUtils.parseIsoLocalDate(iso));
  }

  private String name;
  private String description;
  private String location;
  private Double locationLat;
  private Double locationLon;
  private IllusionEventJoinMode joinMode;
  private Double signUpFee;
  private String signUpFeeCurrency;
  private String signUpFeeText;
  private String start;
  private String end;
  private Integer ageLimit;
  private String imageUrl;
  private Boolean beginnerFriendly;
  private Boolean larpKalenteriSync;
  private String signUpStartDate;
  private String signUpEndDate;
  private List<Genre> genres;
  private Long typeId;
  private List<Long> genreIds;
  private List<SelectItem> typeSelectItems;
}
