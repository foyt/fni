package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Table (
  uniqueConstraints = {
    @UniqueConstraint (columnNames = {"entry_id", "user_id"})
  }    
)
public class CharacterSheetData {
  
  public Long getId() {
    return id;
  }
  
  public CharacterSheetEntry getEntry() {
    return entry;
  }
  
  public void setEntry(CharacterSheetEntry entry) {
    this.entry = entry;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private CharacterSheetEntry entry;

  @ManyToOne(optional = false)
  private User user;
  
  @Lob
  private String value;
}