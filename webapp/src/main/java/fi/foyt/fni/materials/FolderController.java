package fi.foyt.fni.materials;

import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class FolderController {
	
  @Inject
  private FolderDAO folderDAO;

  @Inject
  private MaterialController materialController;
  
  /* Folder */

  public Folder createFolder(Folder parentFolder, String title, User creator) {
    Date now = new Date();
    String urlName = materialController.getUniqueMaterialUrlName(creator, parentFolder, null, title);
    return folderDAO.create(creator, now, creator, now, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE);
  }
  
	public Folder findFolderById(Long folderId) {
		return folderDAO.findById(folderId);
	}
	
}
