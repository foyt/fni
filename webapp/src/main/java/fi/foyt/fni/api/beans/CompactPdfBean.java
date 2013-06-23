package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.Pdf;

public class CompactPdfBean extends CompactMaterialBean {

	public CompactPdfBean(Long id, String urlName, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId) {
	  super(id, MaterialType.PDF, urlName, title, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId);
  }

	public static CompactPdfBean fromEntity(Pdf entity) {
		if (entity == null)
			return null;
		
		Long creatorId = entity.getCreator().getId();
		Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
		Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
		Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
		return new CompactPdfBean(entity.getId(), entity.getUrlName(), entity.getTitle(), entity.getPublicity(), languageId, entity.getModified(), entity.getCreated(), creatorId, modifierId, parentFolderId);
	}
	
	public static List<CompactPdfBean> fromEntities(List<Pdf> pdfs) {
		List<CompactPdfBean> beans = new ArrayList<CompactPdfBean>(pdfs.size());
		
		for (Pdf pdf : pdfs) {
			beans.add(fromEntity(pdf));
		}
		
		return beans;
	}
	
}
