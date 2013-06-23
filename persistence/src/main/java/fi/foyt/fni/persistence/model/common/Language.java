package fi.foyt.fni.persistence.model.common;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Language {

  public Long getId() {
    return id;
  }
  
  public String getISO2() {
    return ISO2;
  }
  
  public void setISO2(String ISO2) {
    this.ISO2 = ISO2;
  }
  
  public String getISO3() {
    return ISO3;
  }
  
  public void setISO3(String ISO3) {
    this.ISO3 = ISO3;
  }
  
  public Boolean getLocalized() {
    return localized;
  }
  
  public void setLocalized(Boolean localized) {
    this.localized = localized;
  }
  
  @Transient
  public Boolean isLocalized() {
    return getLocalized();
  }
  
  @Transient
  public Locale getLocale() {
    return new Locale(getISO2());
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable = false, columnDefinition = "BIT")
  private Boolean localized;
  
  private String ISO2;
  
  private String ISO3;
}
