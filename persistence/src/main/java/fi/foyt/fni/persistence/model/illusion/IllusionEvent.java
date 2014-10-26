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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class IllusionEvent {

  public Long getId() {
    return id;
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
  
  public Date getStartDate() {
    return startDate;
  }
  
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
  
  public Date getStartTime() {
    return startTime;
  }
  
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
  
  public Date getEndDate() {
    return endDate;
  }
  
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
  
  public Date getEndTime() {
    return endTime;
  }
  
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
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

  @Column (nullable = false)
  @Temporal (TemporalType.DATE)
  private Date startDate;
  
  @Temporal (TemporalType.TIME)
  private Date startTime;
  
  @Temporal (TemporalType.DATE)
  private Date endDate;
  
  @Temporal (TemporalType.TIME)
  private Date endTime;
  
}