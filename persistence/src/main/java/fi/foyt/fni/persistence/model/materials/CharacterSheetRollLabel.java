package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
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
   @UniqueConstraint (columnNames = {"sheet_id", "label"})
  }    
)
public class CharacterSheetRollLabel {
  
  public Long getId() {
    return id;
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private CharacterSheet sheet;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  private String label;
}