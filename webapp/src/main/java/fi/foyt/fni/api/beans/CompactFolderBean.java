package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.Folder;

public class CompactFolderBean extends CompactMaterialBean {

	public CompactFolderBean(Long id, String urlName, String title, MaterialPublicity publicity, Long languageId, Date modified, Date created,
      Long creatorId, Long modifierId, Long parentFolderId) {
	  super(id, MaterialType.FOLDER, urlName, title, publicity, languageId, modified, created, creatorId, modifierId, parentFolderId);
  }

	public static CompactFolderBean fromEntity(Folder entity) {
		if (entity == null)
			return null;
		
		Long creatorId = entity.getCreator().getId();
		Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
		Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
		Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
		return new CompactFolderBean(entity.getId(), entity.getUrlName(), entity.getTitle(), entity.getPublicity(), languageId, entity.getModified(), entity.getCreated(), creatorId, modifierId, parentFolderId);
	}
	
	public static List<CompactFolderBean> fromEntities(List<Folder> folders) {
		List<CompactFolderBean> beans = new ArrayList<CompactFolderBean>(folders.size());
		
		for (Folder folder : folders) {
			beans.add(fromEntity(folder));
		}
		
		return beans;
	}
	
}
