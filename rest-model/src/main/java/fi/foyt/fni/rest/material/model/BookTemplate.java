package fi.foyt.fni.rest.material.model;

import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class BookTemplate extends Material {

  public BookTemplate() {
    super();
  }

  public BookTemplate(Long id, MaterialType type, String urlName, String path, String title, MaterialPublicity publicity, Long languageId, Date modified,
      Date created, Long creatorId, Long modifierId, Long parentFolderId, String data, String styles, String fonts, String iconUrl, String description, String license, List<String> tags) {
    super(id, type, urlName, path, title, description, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId, license, tags);
    this.data = data;
    this.fonts = fonts;
    this.styles = styles;
    this.iconUrl = iconUrl;
    this.description = description;
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

  private String data;
  private String styles;
  private String fonts;
  private String iconUrl;
  private String description;
}
