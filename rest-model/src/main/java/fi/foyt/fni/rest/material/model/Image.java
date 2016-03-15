package fi.foyt.fni.rest.material.model;

import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class Image extends Material {
  
  public Image() {
    super();
  }

  public Image(Long id, MaterialType type, String urlName, String path, String title, String description, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId, String license, List<String> tags) {
    super(id, type, urlName, path, title, description, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId, license, tags);
  }

}
