package fi.foyt.fni.materials;

import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.model.materials.Folder;

@Dependent
@Stateful
public class FolderController {
	
	@Inject
	private Logger logger;

  @Inject
  private FolderDAO folderDAO;
  
  /* Folder */
  
	public Folder findFolderById(Long folderId) {
		return folderDAO.findById(folderId);
	}
	
}
