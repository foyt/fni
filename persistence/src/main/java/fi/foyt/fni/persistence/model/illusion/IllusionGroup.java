package fi.foyt.fni.persistence.model.illusion;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class IllusionGroup {

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
  
  public IllusionGroupFolder getFolder() {
    return folder;
  }
  
  public void setFolder(IllusionGroupFolder folder) {
    this.folder = folder;
  }
  
  public IllusionGroupJoinMode getJoinMode() {
    return joinMode;
  }
  
  public void setJoinMode(IllusionGroupJoinMode joinMode) {
    this.joinMode = joinMode;
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
  private IllusionGroupFolder folder;
  
  @Enumerated (EnumType.STRING)
  @Column (nullable = false)
  private IllusionGroupJoinMode joinMode;
}