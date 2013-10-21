package fi.foyt.fni.materials;

import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class VectorImageController {
	
	@Inject
	private Logger logger;

  @Inject
  private VectorImageDAO vectorImageDAO;
  
  /* Document */
  
  public VectorImage createVectorImage(Language language, Folder parentFolder, String urlName, String title, String data, User creator) {
  	return vectorImageDAO.create(creator, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }
  
	public VectorImage findVectorImageById(Long documentId) {
		return vectorImageDAO.findById(documentId);
	}

	public VectorImage updateVectorImageData(VectorImage vectorImage, String data, User modifier) {
		return vectorImageDAO.updateData(vectorImage, modifier, data);
	}

	public VectorImage updateVectorImageTitle(VectorImage vectorImage, String title, User modifier) {
		return vectorImageDAO.updateTitle(vectorImage, modifier, title);
	}

}
