package fi.foyt.fni.rest.material.model;

import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class Document extends Material {
  
  public Document() {
    super();
  }

  public Document(Long id, MaterialType type, String urlName, String path, String title, String description, MaterialPublicity publicity, 
      Long languageId, Date modified, Date created, Long creatorId, Long modifierId, Long parentFolderId, String data, String license, List<String> tags) {
    super(id, type, urlName, path, title, description, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId, license, tags);
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
