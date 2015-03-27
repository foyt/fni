package fi.foyt.fni.materials;

import fi.foyt.fni.persistence.model.materials.CharacterSheetEntryType;

public class CharacterSheetMetaField {

  public CharacterSheetMetaField() {
  }

  public CharacterSheetMetaField(CharacterSheetEntryType type, String label) {
    super();
    this.label = label;
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public CharacterSheetEntryType getType() {
    return type;
  }

  public void setType(CharacterSheetEntryType type) {
    this.type = type;
  }

  private String label;
  private CharacterSheetEntryType type;
}
