package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table (
    uniqueConstraints = {
      @UniqueConstraint (columnNames = {"name", "sheet_id"})
    }    
  )
public class CharacterSheetEntry {

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CharacterSheetEntryType getType() {
    return type;
  }

  public void setType(CharacterSheetEntryType type) {
    this.type = type;
  }

  public CharacterSheet getSheet() {
    return sheet;
  }

  public void setSheet(CharacterSheet sheet) {
    this.sheet = sheet;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotNull
  @NotEmpty
  private String name;

  @Column(nullable = false)
  @NotNull
  @Enumerated(EnumType.STRING)
  private CharacterSheetEntryType type;

  @ManyToOne(optional = false)
  private CharacterSheet sheet;
}