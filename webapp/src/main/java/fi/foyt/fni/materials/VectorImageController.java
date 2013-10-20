package fi.foyt.fni.materials;

import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.model.materials.VectorImage;

@Dependent
@Stateful
public class VectorImageController {
	
	@Inject
	private Logger logger;

  @Inject
  private VectorImageDAO vectorImageDAO;
  
  /* Document */
  
	public VectorImage findVectorImageById(Long documentId) {
		return vectorImageDAO.findById(documentId);
	}

}
