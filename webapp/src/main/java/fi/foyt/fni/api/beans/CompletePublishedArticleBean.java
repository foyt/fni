package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.PublishedArticle;
import fi.foyt.fni.persistence.model.materials.PublishedArticleType;

public class CompletePublishedArticleBean {

	public CompletePublishedArticleBean(Long id, Date modified, Date created, CompactUserBean creator, CompactUserBean modifier, CompactMaterialBean material, PublishedArticleType type) {
		this.id = id;
		this.modified = modified;
		this.created = created;
		this.creator = creator;
		this.modifier = modifier;
		this.material = material;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public CompactUserBean getCreator() {
	  return creator;
  }
	
	public void setCreator(CompactUserBean creator) {
	  this.creator = creator;
  }
	
	public CompactUserBean getModifier() {
	  return modifier;
  }
	
	public void setModifier(CompactUserBean modifier) {
	  this.modifier = modifier;
  }

	public CompactMaterialBean getMaterial() {
		return material;
	}

	public void setMaterial(CompactMaterialBean material) {
		this.material = material;
	}

	public PublishedArticleType getType() {
		return type;
	}

	public void setType(PublishedArticleType type) {
		this.type = type;
	}


	
	public static CompletePublishedArticleBean fromEntity(PublishedArticle entity) {
		if (entity == null)
			return null;
		
		return new CompletePublishedArticleBean(entity.getId(), entity.getModified(), entity.getCreated(), CompactUserBean.fromEntity(entity.getCreator()), CompactUserBean.fromEntity(entity.getModifier()), CompactMaterialBean.fromMaterialEntity(entity.getMaterial()), entity.getType());
	}

	public static List<CompletePublishedArticleBean> fromEntities(List<PublishedArticle> entities) {
		List<CompletePublishedArticleBean> beans = new ArrayList<CompletePublishedArticleBean>(entities.size());

		for (PublishedArticle entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	private Long id;

	private Date modified;

	private Date created;

	private CompactUserBean creator;

	private CompactUserBean modifier;

	private CompactMaterialBean material;

	private PublishedArticleType type;
}
