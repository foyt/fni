package fi.foyt.fni.rest.material.model;

import java.util.Date;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class Image extends Material {
  
  public Image() {
    super();
  }

  public Image(Long id, MaterialType type, String urlName, String path, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId) {
    super(id, type, urlName, path, title, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId);
  }

}
