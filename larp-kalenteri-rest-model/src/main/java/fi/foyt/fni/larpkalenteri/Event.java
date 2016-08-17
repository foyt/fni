package fi.foyt.fni.larpkalenteri;


import java.time.ZonedDateTime;
import java.util.List;

public class Event {
  
  public Event() {
  }
  
  public Event(Long id, String name, String type, ZonedDateTime start, ZonedDateTime end, String textDate, ZonedDateTime signUpStart, ZonedDateTime signUpEnd, Long locationDropDown,
      String location, String iconURL, List<String> genres, String cost, Integer ageLimit, Boolean beginnerFriendly, String storyDescription,
      String infoDescription, String organizerName, String organizerEmail, String link1, String link2, Status status, String password, Boolean eventFull,
      Boolean invitationOnly, Boolean languageFree, Long illusionId) {
    super();
    this.id = id;
    this.name = name;
    this.type = type;
    this.start = start;
    this.end = end;
    this.textDate = textDate;
    this.signUpStart = signUpStart;
    this.signUpEnd = signUpEnd;
    this.locationDropDown = locationDropDown;
    this.location = location;
    this.iconURL = iconURL;
    this.genres = genres;
    this.cost = cost;
    this.ageLimit = ageLimit;
    this.beginnerFriendly = beginnerFriendly;
    this.storyDescription = storyDescription;
    this.infoDescription = infoDescription;
    this.organizerName = organizerName;
    this.organizerEmail = organizerEmail;
    this.link1 = link1;
    this.link2 = link2;
    this.status = status;
    this.password = password;
    this.eventFull = eventFull;
    this.invitationOnly = invitationOnly;
    this.languageFree = languageFree;
    this.illusionId = illusionId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ZonedDateTime getStart() {
    return start;
  }

  public void setStart(ZonedDateTime start) {
    this.start = start;
  }

  public ZonedDateTime getEnd() {
    return end;
  }

  public void setEnd(ZonedDateTime end) {
    this.end = end;
  }

  public String getTextDate() {
    return textDate;
  }

  public void setTextDate(String textDate) {
    this.textDate = textDate;
  }

  public ZonedDateTime getSignUpStart() {
    return signUpStart;
  }

  public void setSignUpStart(ZonedDateTime signUpStart) {
    this.signUpStart = signUpStart;
  }

  public ZonedDateTime getSignUpEnd() {
    return signUpEnd;
  }

  public void setSignUpEnd(ZonedDateTime signUpEnd) {
    this.signUpEnd = signUpEnd;
  }

  public Long getLocationDropDown() {
    return locationDropDown;
  }

  public void setLocationDropDown(Long locationDropDown) {
    this.locationDropDown = locationDropDown;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getIconURL() {
    return iconURL;
  }

  public void setIconURL(String iconURL) {
    this.iconURL = iconURL;
  }

  public List<String> getGenres() {
    return genres;
  }

  public void setGenres(List<String> genres) {
    this.genres = genres;
  }

  public String getCost() {
    return cost;
  }

  public void setCost(String cost) {
    this.cost = cost;
  }

  public Integer getAgeLimit() {
    return ageLimit;
  }

  public void setAgeLimit(Integer ageLimit) {
    this.ageLimit = ageLimit;
  }

  public Boolean getBeginnerFriendly() {
    return beginnerFriendly;
  }

  public void setBeginnerFriendly(Boolean beginnerFriendly) {
    this.beginnerFriendly = beginnerFriendly;
  }

  public String getStoryDescription() {
    return storyDescription;
  }

  public void setStoryDescription(String storyDescription) {
    this.storyDescription = storyDescription;
  }

  public String getInfoDescription() {
    return infoDescription;
  }

  public void setInfoDescription(String infoDescription) {
    this.infoDescription = infoDescription;
  }

  public String getOrganizerName() {
    return organizerName;
  }

  public void setOrganizerName(String organizerName) {
    this.organizerName = organizerName;
  }

  public String getOrganizerEmail() {
    return organizerEmail;
  }

  public void setOrganizerEmail(String organizerEmail) {
    this.organizerEmail = organizerEmail;
  }

  public String getLink1() {
    return link1;
  }

  public void setLink1(String link1) {
    this.link1 = link1;
  }

  public String getLink2() {
    return link2;
  }

  public void setLink2(String link2) {
    this.link2 = link2;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getEventFull() {
    return eventFull;
  }

  public void setEventFull(Boolean eventFull) {
    this.eventFull = eventFull;
  }

  public Boolean getInvitationOnly() {
    return invitationOnly;
  }

  public void setInvitationOnly(Boolean invitationOnly) {
    this.invitationOnly = invitationOnly;
  }

  public Boolean getLanguageFree() {
    return languageFree;
  }

  public void setLanguageFree(Boolean languageFree) {
    this.languageFree = languageFree;
  }

  public Long getIllusionId() {
    return illusionId;
  }

  public void setIllusionId(Long illusionId) {
    this.illusionId = illusionId;
  }

  private Long id;
  private String name;
  private String type;
  private ZonedDateTime start;
  private ZonedDateTime end;
  private String textDate;
  private ZonedDateTime signUpStart;
  private ZonedDateTime signUpEnd;
  private Long locationDropDown;
  private String location;
  private String iconURL;
  private List<String> genres;
  private String cost;
  private Integer ageLimit;
  private Boolean beginnerFriendly;
  private String storyDescription;
  private String infoDescription;
  private String organizerName;
  private String organizerEmail;
  private String link1;
  private String link2;
  private Status status;
  private String password;
  private Boolean eventFull;
  private Boolean invitationOnly;
  private Boolean languageFree;
  private Long illusionId;

  public enum Status {
    ACTIVE,
    MODIFIED,
    PENDING,
    CANCELLED
  }
  
}

