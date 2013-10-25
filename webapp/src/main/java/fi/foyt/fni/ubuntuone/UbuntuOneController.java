package fi.foyt.fni.ubuntuone;

import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneRootFolderDAO;
import fi.foyt.fni.persistence.model.materials.UbuntuOneRootFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class UbuntuOneController {
	
	@Inject
	private MaterialController materialController;

	@Inject
	private UbuntuOneRootFolderDAO ubuntuOneRootFolderDAO;
	
	public UbuntuOneRootFolder createUbuntuOneRootFolder(User creator, String title) {
		Date now = new Date();
    String urlName = materialController.getUniqueMaterialUrlName(creator, null, null, title);
		return ubuntuOneRootFolderDAO.create(creator, now, creator, now, null, urlName, title, MaterialPublicity.PRIVATE, null, null);
	}
	
	public UbuntuOneRootFolder findUbuntuOneRootFolderByUser(User user) {
	  return ubuntuOneRootFolderDAO.findByUser(user);
	}
	
}
