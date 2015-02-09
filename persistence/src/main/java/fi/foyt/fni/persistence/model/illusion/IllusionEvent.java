package fi.foyt.fni.persistence.model.illusion;

import java.util.Currency;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class IllusionEvent {

  public Long getId() {
    return id;
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
  
  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public String getXmppRoom() {
    return xmppRoom;
  }

  public void setXmppRoom(String xmppRoom) {
    this.xmppRoom = xmppRoom;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Date getCreated() {
    return created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public IllusionEventFolder getFolder() {
    return folder;
  }
  
  public void setFolder(IllusionEventFolder folder) {
    this.folder = folder;
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
  
  public Currency getSignUpFeeCurrency() {
    return signUpFeeCurrency;
  }
  
  public void setSignUpFeeCurrency(Currency signUpFeeCurrency) {
    this.signUpFeeCurrency = signUpFeeCurrency;
  }
  
  public String getLocation() {
    return location;
  }
  
  public void setLocation(String location) {
    this.location = location;
  }
  
  public String getDomain() {
    return domain;
  }
  
  public void setDomain(String domain) {
    this.domain = domain;
  }
  
  public Date getStart() {
    return start;
  }
  
  public void setStart(Date start) {
    this.start = start;
  }
  
  public Date getEnd() {
    return end;
  }
  
  public void setEnd(Date end) {
    this.end = end;
  }
  
  public OAuthClient getOAuthClient() {
    return oAuthClient;
  }
  
  public void setOAuthClient(OAuthClient oAuthClient) {
    this.oAuthClient = oAuthClient;
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public IllusionEventType getType() {
    return type;
  }

  public void setType(IllusionEventType type) {
    this.type = type;
  }
  
  public Date getSignUpStartDate() {
    return signUpStartDate;
  }
  
  public void setSignUpStartDate(Date signUpStartDate) {
    this.signUpStartDate = signUpStartDate;
  }
  
  public Date getSignUpEndDate() {
    return signUpEndDate;
  }
  
  public void setSignUpEndDate(Date signUpEndDate) {
    this.signUpEndDate = signUpEndDate;
  }
  
  public ForumTopic getForumTopic() {
    return forumTopic;
  }
  
  public void setForumTopic(ForumTopic forumTopic) {
    this.forumTopic = forumTopic;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable = false, columnDefinition = "BIT")
  private Boolean published;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  private String name;
  
  @Lob
  private String description;
  
  @NotNull
  @Column (nullable = false)
  private Date created;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false, unique = true)
  private String urlName;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false, unique = true)
  private String xmppRoom;
  
  @OneToOne
  private IllusionEventFolder folder;
  
  @Enumerated (EnumType.STRING)
  @Column (nullable = false)
  private IllusionEventJoinMode joinMode;
  
  private Double signUpFee;
  
  private Currency signUpFeeCurrency; 
  
  private String location;
  
  private Integer ageLimit;
  
  private Boolean beginnerFriendly;
  
  private String imageUrl;

  @ManyToOne
  private IllusionEventType type;

  @Temporal (TemporalType.DATE)
  private Date signUpStartDate;
  
  @Temporal (TemporalType.DATE)
  private Date signUpEndDate;

  @Column (unique = true)
  private String domain;

  @Column (nullable = false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date start;

  @Column (nullable = false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date end;

  @ManyToOne
  private OAuthClient oAuthClient;
  
  @ManyToOne
  private ForumTopic forumTopic;
}