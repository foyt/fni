package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class CompactMaterialBean {
	
	public CompactMaterialBean(Long id, MaterialType type, String urlName, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId) {
	  this.id = id;
	  this.type = type;
	  this.urlName = urlName;
	  this.title = title;
	  this.publicity = publicity;
	  this.languageId = languageId;
	  this.modified = modified;
	  this.created = created;
	  this.creatorId = creatorId;
	  this.modifierId = modifierId;
	  this.parentFolderId = parentFolderId;
  }

	public Long getId() {
  	return id;
  }

	public void setId(Long id) {
  	this.id = id;
  }

	public MaterialType getType() {
  	return type;
  }

	public void setType(MaterialType type) {
  	this.type = type;
  }

	public String getUrlName() {
  	return urlName;
  }

	public void setUrlName(String urlName) {
  	this.urlName = urlName;
  }

	public String getTitle() {
  	return title;
  }

	public void setTitle(String title) {
  	this.title = title;
  }

	public MaterialPublicity getPublicity() {
  	return publicity;
  }

	public void setPublicity(MaterialPublicity publicity) {
  	this.publicity = publicity;
  }

	public Long getLanguageId() {
  	return languageId;
  }

	public void setLanguageId(Long languageId) {
  	this.languageId = languageId;
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

	public Long getCreatorId() {
  	return creatorId;
  }

	public void setCreatorId(Long creatorId) {
  	this.creatorId = creatorId;
  }

	public Long getModifierId() {
  	return modifierId;
  }

	public void setModifierId(Long modifierId) {
  	this.modifierId = modifierId;
  }
	
	public Long getParentFolderId() {
  	return parentFolderId;
  }

	public void setParentFolderId(Long parentFolderId) {
  	this.parentFolderId = parentFolderId;
  }

	public static CompactMaterialBean fromMaterialEntity(Material entity) {
		if (entity == null)
			return null;
		
		Long creatorId = entity.getCreator().getId();
		Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
		Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
		Long modifierId = null;
		if (entity.getModifier() != null) {
			modifierId = entity.getModifier().getId();
		}

		return new CompactMaterialBean(entity.getId(), entity.getType(), entity.getUrlName(), entity.getTitle(), entity.getPublicity(), languageId, entity.getModified(), entity.getCreated(), creatorId, modifierId, parentFolderId);
	}
	
	public static List<CompactMaterialBean> fromMaterialEntities(List<Material> materials) {
		List<CompactMaterialBean> beans = new ArrayList<CompactMaterialBean>(materials.size());
		
		for (Material material : materials) {
			beans.add(fromMaterialEntity(material));
		}
		
		return beans;
	}

	private Long id;
  
  private MaterialType type;
  
  private String urlName;
  
  private String title;
  
  private MaterialPublicity publicity;
  
  private Long languageId;
  
  private Date modified;
  
  private Date created;

  private Long creatorId;

  private Long modifierId;
  
  private Long parentFolderId;
}
