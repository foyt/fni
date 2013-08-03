package fi.foyt.fni.persistence.model.users;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.materials.Image;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Indexed
public class User {
  
  public Long getId() {
    return id;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getNickname() {
    return nickname;
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  
  public UserRole getRole() {
	  return role;
  }
  
  public void setRole(UserRole role) {
	  this.role = role;
  }

  public Date getRegistrationDate() {
    return registrationDate;
  }
  
  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }
  
  public String getLocale() {
    return locale;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  public Date getPremiumExpires() {
    return premiumExpires;
  }
  
  public void setPremiumExpires(Date premiumExpires) {
    this.premiumExpires = premiumExpires;
  }
  
  public Boolean getArchived() {
    return archived;
  }
  
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Image getProfileImage() {
    return profileImage;
  }
  
  public void setProfileImage(Image profileImage) {
    this.profileImage = profileImage;
  }
  
  public String getAbout() {
		return about;
	}
  
  public void setAbout(String about) {
		this.about = about;
	}
  
  @Transient
  @Field
  public String getFullName() {
    if (getFirstName() != null && getLastName() != null)
      return new StringBuilder(getFirstName()).append(' ').append(getLastName()).toString();
    else
      return null;
  }
  
  @Id
  @DocumentId
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable=false)
  @NotNull
  @NotEmpty
  @Field
  private String firstName;
  
  @Column (nullable=false)
  @NotNull
  @NotEmpty
  @Field
  private String lastName;  
  
  private String nickname;  
  
  @Lob
  private String about;
  
  @Column (nullable=false)
  @Enumerated (EnumType.STRING)
  private UserRole role;
  
  @Column (nullable=false)
  private Date registrationDate;
  
  @Column (nullable=false)
  private String locale;
  
  private Date premiumExpires;
  
  @Column (nullable=false, columnDefinition = "BIT")
  private Boolean archived;
  
  @ManyToOne
  private Image profileImage;
    
  // TODO: License
}