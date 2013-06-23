package fi.foyt.fni.materials;

import java.util.Date;

import fi.foyt.fni.persistence.model.materials.MaterialType;

public class MaterialBean {

  public MaterialBean(Long id, String title, MaterialType type, MaterialArchetype archetype, String mimeType, Date modified, Date created) {
    this.id = id;
    this.title = title;
    this.type = type;
    this.archetype = archetype;
    this.mimeType = mimeType;
    this.modified = modified;
    this.created = created;
  }
  
  public MaterialBean(MaterialBean materialBean) {
    this(materialBean.getId(), materialBean.getTitle(), materialBean.getType(), materialBean.getArchetype(), materialBean.getMimeType(), materialBean.getModified(), materialBean.getCreated());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public MaterialType getType() {
    return type;
  }

  public void setType(MaterialType type) {
    this.type = type;
  }

  public MaterialArchetype getArchetype() {
    return archetype;
  }

  public void setArchetype(MaterialArchetype archetype) {
    this.archetype = archetype;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  private Long id;

  private String title;

  private MaterialType type;

  private MaterialArchetype archetype;

  private String mimeType;

  private Date modified;

  private Date created;
}
