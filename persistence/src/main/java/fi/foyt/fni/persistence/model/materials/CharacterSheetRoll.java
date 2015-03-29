package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class CharacterSheetRoll {
  
  public Long getId() {
    return id;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public CharacterSheet getSheet() {
    return sheet;
  }
  
  public void setSheet(CharacterSheet sheet) {
    this.sheet = sheet;
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String label) {
    this.label = label;
  }
  
  public String getRoll() {
    return roll;
  }
  
  public void setRoll(String roll) {
    this.roll = roll;
  }
  
  public Integer getResult() {
    return result;
  }
  
  public void setResult(Integer result) {
    this.result = result;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private CharacterSheet sheet;

  @ManyToOne(optional = false)
  private User user;
  
  private String label;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  private String roll;
  
  @NotNull
  @Column (nullable = false)
  private Integer result;
}