package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
  
  public Date getTime() {
    return time;
  }
  
  public void setTime(Date time) {
    this.time = time;
  }
  
  public CharacterSheetRollLabel getLabel() {
    return label;
  }
  
  public void setLabel(CharacterSheetRollLabel label) {
    this.label = label;
  }
  
  public Integer getResult() {
    return result;
  }
  
  public void setResult(Integer result) {
    this.result = result;
  }
  
  public String getRoll() {
    return roll;
  }
  
  public void setRoll(String roll) {
    this.roll = roll;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private CharacterSheetRollLabel label;

  @ManyToOne(optional = false)
  private User user;
  
  @NotNull
  @Column (nullable = false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date time;
  
  @NotNull
  @Column (nullable = false)
  private Integer result;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  private String roll;
}