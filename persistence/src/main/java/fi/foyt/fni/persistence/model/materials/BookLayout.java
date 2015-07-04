package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class BookLayout extends Material {
  
  public BookLayout() {
    setType(MaterialType.BOOK_LAYOUT);
  }
  
  public String getData() {
		return data;
	}
  
  public void setData(String data) {
		this.data = data;
	}
  
  public String getStyles() {
    return styles;
  }
  
  public void setStyles(String styles) {
    this.styles = styles;
  }
  
  public String getFonts() {
    return fonts;
  }
  
  public void setFonts(String fonts) {
    this.fonts = fonts;
  }
  
  @Column 
  @Lob
  private String data;
  
  @Column 
  @Lob
  private String styles;
  
  @Column 
  @Lob
  private String fonts;
}