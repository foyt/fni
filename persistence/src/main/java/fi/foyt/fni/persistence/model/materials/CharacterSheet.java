package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class CharacterSheet extends Material {
  
  public CharacterSheet() {
    setType(MaterialType.CHARACTER_SHEET);
  }
  
  public String getContents() {
    return contents;
  }
  
  public void setContents(String contents) {
    this.contents = contents;
  }
  
  public String getStyles() {
    return styles;
  }
  
  public void setStyles(String styles) {
    this.styles = styles;
  }
  
  public String getScripts() {
    return scripts;
  }
  
  public void setScripts(String scripts) {
    this.scripts = scripts;
  }
  
  @Lob
  private String contents;

  @Lob
  private String styles;

  @Lob
  private String scripts;
}