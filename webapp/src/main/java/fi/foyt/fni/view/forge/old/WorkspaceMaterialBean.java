package fi.foyt.fni.view.forge.old;

import java.util.Date;

import fi.foyt.fni.materials.MaterialArchetype;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class WorkspaceMaterialBean {

  public WorkspaceMaterialBean(Long id, Long parentId, String parentTitle, String title, MaterialType type, 
  		MaterialArchetype archetype, String mimeType, Date modified, Date created, String path, String editorName, 
  		boolean starred, boolean editable, boolean movable, boolean deletable, boolean shareable, boolean printableAsPdf) {
  	
    this.id = id;
    this.parentId = parentId;
    this.parentTitle = parentTitle;
    this.title = title;
    this.type = type;
    this.archetype = archetype;
    this.mimeType = mimeType;
    this.modified = modified;
    this.created = created;
    this.path = path;
    this.editorName = editorName;
    this.starred = starred;
    this.movable = movable;
    this.editable = editable;
    this.deletable = deletable;
    this.shareable = shareable;
    this.printableAsPdf = printableAsPdf;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getParentId() {
		return parentId;
	}
  
  public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

  public String getParentTitle() {
		return parentTitle;
	}
  
  public void setParentTitle(String parentTitle) {
		this.parentTitle = parentTitle;
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

  public void setEditorName(String editorName) {
    this.editorName = editorName;
  }

  public void setStarred(boolean starred) {
    this.starred = starred;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }
  
  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }

  public boolean getStarred() {
    return starred;
  }

  public String getEditorName() {
    return editorName;
  }

  public boolean getEditable() {
    return editable;
  }

  public boolean getShareable() {
    return shareable;
  }
  
  public boolean getPrintableAsPdf() {
    return printableAsPdf;
  }

  public boolean getDeletable() {
    return deletable;
  }
  
  public boolean getMovable() {
    return movable;
  }

  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
  
  private Long id;
  private Long parentId;
  private String parentTitle;
  private String title;
  private MaterialType type;
  private MaterialArchetype archetype;
  private String mimeType;
  private Date modified;
  private Date created;
  private String path;
  private String editorName;
  private boolean starred;
  private boolean editable;
  private boolean deletable;
  private boolean movable;
  private boolean shareable; 
  private boolean printableAsPdf;
}