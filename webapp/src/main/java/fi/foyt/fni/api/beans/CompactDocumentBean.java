package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class CompactDocumentBean extends CompactMaterialBean {

	public CompactDocumentBean(Long id, String urlName, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId) {
	  super(id, MaterialType.DOCUMENT, urlName, title, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId);
  }

	public static CompactDocumentBean fromEntity(Document entity) {
		if (entity == null)
			return null;
		
		Long creatorId = entity.getCreator().getId();
		Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
		Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
		Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
		return new CompactDocumentBean(entity.getId(), entity.getUrlName(), entity.getTitle(), entity.getPublicity(), languageId, entity.getModified(), entity.getCreated(), creatorId, modifierId, parentFolderId);
	}
	
	public static List<CompactDocumentBean> fromEntities(List<Document> documents) {
		List<CompactDocumentBean> beans = new ArrayList<CompactDocumentBean>(documents.size());
		
		for (Document document : documents) {
			beans.add(fromEntity(document));
		}
		
		return beans;
	}
	
}
