package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class BookTemplate extends Material {
  
  public BookTemplate() {
    setType(MaterialType.BOOK_TEMPLATE);
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
  
  public String getIconUrl() {
    return iconUrl;
  }
  
  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
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
  
  @Lob
  private String description;
  
  private String iconUrl;
}