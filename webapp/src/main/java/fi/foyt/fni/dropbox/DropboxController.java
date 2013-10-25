package fi.foyt.fni.dropbox;

import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.materials.DropboxRootFolderDAO;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class DropboxController {
	
	@Inject
	private MaterialController materialController;

	@Inject
	private DropboxRootFolderDAO dropboxRootFolderDAO;
	
	public DropboxRootFolder createDropboxRootFolder(User creator, String title) {
		Date now = new Date();
    String urlName = materialController.getUniqueMaterialUrlName(creator, null, null, title);
		return dropboxRootFolderDAO.create(creator, now, creator, now, null, urlName, title, MaterialPublicity.PRIVATE, null, null);
	}
	
	public DropboxRootFolder findDropboxRootFolderByUser(User user) {
	  return dropboxRootFolderDAO.findByUser(user);
	}
	
}
