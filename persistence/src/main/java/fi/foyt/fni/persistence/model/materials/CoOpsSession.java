package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CoOpsSession {

  public Long getId() {
    return id;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  
  public CoOpsSessionType getType() {
    return type;
  }
  
  public void setType(CoOpsSessionType type) {
    this.type = type;
  }
  
  public Boolean getClosed() {
    return closed;
  }
  
  public void setClosed(Boolean closed) {
    this.closed = closed;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Material getMaterial() {
    return material;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  public String getAlgorithm() {
    return algorithm;
  }
  
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }
  
  public Long getJoinRevision() {
    return joinRevision;
  }
  
  public void setJoinRevision(Long joinRevision) {
    this.joinRevision = joinRevision;
  }
  
  public Date getAccessed() {
    return accessed;
  }
  
  public void setAccessed(Date accessed) {
    this.accessed = accessed;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable = false, unique = true)
  @NotEmpty
  @NotNull
  private String sessionId;
  
  @Column (nullable = false)
  @NotNull
  @Enumerated (EnumType.STRING)
  private CoOpsSessionType type;
  
  @Column (nullable = false)
  @NotNull
  private Boolean closed;

  @ManyToOne
  private User user;

  @ManyToOne
  private Material material;
  
  @Column (updatable = false, nullable = false)
  @NotNull
  @NotEmpty
  private String algorithm;
  
  @Column (updatable = false, nullable = false)
  @NotNull
  private Long joinRevision;
  
  @Column (nullable = false)
  @NotNull
  private Date accessed;
}