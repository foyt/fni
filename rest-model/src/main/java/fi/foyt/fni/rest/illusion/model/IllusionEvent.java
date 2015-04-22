package fi.foyt.fni.rest.illusion.model;

import java.util.List;

import org.joda.time.DateTime;

import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;

public class IllusionEvent {

  public IllusionEvent() {
  }
  
  public IllusionEvent(Long id, Boolean published, String name, String description, DateTime created, String urlName, String xmppRoom, IllusionEventJoinMode joinMode,
      String signUpFeeText, Double signUpFee, String signUpFeeCurrency, String location, Integer ageLimit, Boolean beginnerFriendly, String imageUrl, Long typeId,
      DateTime signUpStartDate, DateTime signUpEndDate, String domain, DateTime start, DateTime end, List<Long> genreIds) {
    this.id = id;
    this.published = published;
    this.name = name;
    this.description = description;
    this.created = created;
    this.urlName = urlName;
    this.xmppRoom = xmppRoom;
    this.joinMode = joinMode;
    this.signUpFeeText = signUpFeeText;
    this.signUpFee = signUpFee;
    this.signUpFeeCurrency = signUpFeeCurrency;
    this.location = location;
    this.ageLimit = ageLimit;
    this.beginnerFriendly = beginnerFriendly;
    this.imageUrl = imageUrl;
    this.typeId = typeId;
    this.signUpStartDate = signUpStartDate;
    this.signUpEndDate = signUpEndDate;
    this.domain = domain;
    this.start = start;
    this.end = end;
    this.genreIds = genreIds;
  }

  /**
   * Returns event's id
   * 
   * @return event's id
   */
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Returns whether event is public or not 
   * 
   * @return whether event is public or not 
   */
  public Boolean getPublished() {
    return published;
  }
  
  public void setPublished(Boolean published) {
    this.published = published;
  }

  /**
   * Returns event's name 
   * 
   * @return event's name 
   * @requiredField
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns event's description
   * 
   * @return event's description
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns event's creation time in ISO 8601 format
   * 
   * @return event's creation time in ISO 8601 format
   */
  public DateTime getCreated() {
    return created;
  }
  
  public void setCreated(DateTime created) {
    this.created = created;
  }

  /**
   * Returns event's URL name 
   * 
   * @return event's URL name
   */
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  /**
   * Returns event's XMPP group chat room
   * 
   * @return event's XMPP group chat room
   */
  public String getXmppRoom() {
    return xmppRoom;
  }

  public void setXmppRoom(String xmppRoom) {
    this.xmppRoom = xmppRoom;
  }

  /**
   * Returns event's join mode
   * 
   * @return event's join mode
   * @requiredField
   */
  public IllusionEventJoinMode getJoinMode() {
    return joinMode;
  }

  public void setJoinMode(IllusionEventJoinMode joinMode) {
    this.joinMode = joinMode;
  }
  
  /**
   * Returns event's sign-up fee free text
   * 
   * @return event's sign-up fee free text
   */
  public String getSignUpFeeText() {
    return signUpFeeText;
  }
  
  public void setSignUpFeeText(String signUpFeeText) {
    this.signUpFeeText = signUpFeeText;
  }

  /**
   * Returns event's sign-up fee
   * 
   * @return event's sign-up fee
   */
  public Double getSignUpFee() {
    return signUpFee;
  }

  public void setSignUpFee(Double signUpFee) {
    this.signUpFee = signUpFee;
  }

  /**
   * Returns event's sign-up fee currency in ISO 4217 format
   * 
   * @return event's sign-up fee currency in ISO 4217 format
   */
  public String getSignUpFeeCurrency() {
    return signUpFeeCurrency;
  }

  public void setSignUpFeeCurrency(String signUpFeeCurrency) {
    this.signUpFeeCurrency = signUpFeeCurrency;
  }

  /**
   * Returns event's location
   * 
   * @return event's location
   */
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns event's age limit
   * 
   * @return event's age limit
   */
  public Integer getAgeLimit() {
    return ageLimit;
  }

  public void setAgeLimit(Integer ageLimit) {
    this.ageLimit = ageLimit;
  }

  /**
   * Returns whether event is beginner friendly
   * 
   * @return whether event is beginner friendly
   * @requiredField
   */
  public Boolean getBeginnerFriendly() {
    return beginnerFriendly;
  }

  public void setBeginnerFriendly(Boolean beginnerFriendly) {
    this.beginnerFriendly = beginnerFriendly;
  }

  /**
   * Returns a URL into event's image
   * 
   * @return a URL into event's image
   */
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * Returns id of the event's type
   * 
   * @return id of the event's type
   * @requiredField
   */
  public Long getTypeId() {
    return typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  /**
   * Returns event's sign-up start date in ISO 8601 format
   * 
   * @return event's sign-up start date in ISO 8601 format
   */
  public DateTime getSignUpStartDate() {
    return signUpStartDate;
  }
  
  public void setSignUpStartDate(DateTime signUpStartDate) {
    this.signUpStartDate = signUpStartDate;
  }

  /**
   * Returns event's sign-up end date in ISO 8601 format
   * 
   * @return event's sign-up end date in ISO 8601 format
   */
  public DateTime getSignUpEndDate() {
    return signUpEndDate;
  }
  
  public void setSignUpEndDate(DateTime signUpEndDate) {
    this.signUpEndDate = signUpEndDate;
  }

  /**
   * Returns event's custom domain or null when event is not bound to custom domain
   * 
   * @return event's custom domain or null when event is not bound to custom domain
   */
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }
  
  /**
   * Returns event's start time in ISO 8601 format
   * 
   * @return event's start time in ISO 8601 format
   */
  public DateTime getStart() {
    return start;
  }
  
  public void setStart(DateTime start) {
    this.start = start;
  }

  /**
   * Returns event's end time in ISO 8601 format
   * 
   * @return event's end time in ISO 8601 format
   */
  public DateTime getEnd() {
    return end;
  }
  
  public void setEnd(DateTime end) {
    this.end = end;
  }
  
  /**
   * Returns a List of genre ids
   * 
   * @return List of genre ids
   */
  public List<Long> getGenreIds() {
    return genreIds;
  }

  public void setGenreIds(List<Long> genreIds) {
    this.genreIds = genreIds;
  }

  private Long id;
  private Boolean published;
  private String name;
  private String description;
  private DateTime created;
  private String urlName;
  private String xmppRoom;
  private IllusionEventJoinMode joinMode;
  private String signUpFeeText;
  private Double signUpFee;
  private String signUpFeeCurrency;
  private String location;
  private Integer ageLimit;
  private Boolean beginnerFriendly;
  private String imageUrl;
  private Long typeId;
  private DateTime signUpStartDate;
  private DateTime signUpEndDate;
  private String domain;
  private DateTime start;
  private DateTime end;
  private List<Long> genreIds;
}
