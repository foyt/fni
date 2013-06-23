package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class MaterialView {

  public Long getId() {
    return id;
  }

  public Material getMaterial() {
    return material;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Integer getCount() {
    return count;
  }
  
  public void setCount(Integer count) {
    this.count = count;
  }
  
  public Date getViewed() {
    return viewed;
  }
  
  public void setViewed(Date viewed) {
    this.viewed = viewed;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private Material material;
  
  @ManyToOne
  private User user;
  
  @Column(nullable = false)
  private Date viewed;
  
  @Column(nullable = false)
  private Integer count;
}
