package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.VectorImage;

public class CompactVectorImageBean extends CompactMaterialBean {

	public CompactVectorImageBean(Long id, String urlName, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId) {
	  super(id, MaterialType.VECTOR_IMAGE, urlName, title, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId);
  }

	public static CompactVectorImageBean fromEntity(VectorImage entity) {
		if (entity == null)
			return null;
		
		Long creatorId = entity.getCreator().getId();
		Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
		Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
		Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
		return new CompactVectorImageBean(entity.getId(), entity.getUrlName(), entity.getTitle(), entity.getPublicity(), languageId, entity.getModified(), entity.getCreated(), creatorId, modifierId, parentFolderId);
	}
	
	public static List<CompactVectorImageBean> fromEntities(List<VectorImage> vectorImages) {
		List<CompactVectorImageBean> beans = new ArrayList<CompactVectorImageBean>(vectorImages.size());
		
		for (VectorImage vectorImage : vectorImages) {
			beans.add(fromEntity(vectorImage));
		}
		
		return beans;
	}
	
}
