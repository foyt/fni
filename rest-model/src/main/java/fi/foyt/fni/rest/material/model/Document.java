package fi.foyt.fni.rest.material.model;

import java.util.Date;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class Document extends Material {
  
  public Document() {
    super();
  }

  public Document(Long id, MaterialType type, String urlName, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId, String data) {
    super(id, type, urlName, title, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId);
    this.data = data;
  }

  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  private String data;
}
