package fi.foyt.fni.materials;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.PermaLinkDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.PermaLink;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class VectorImageController {

  @Inject
  private MaterialController materialController;

  @Inject
	private MaterialDAO materialDAO;
	
  @Inject
  private VectorImageDAO vectorImageDAO;

  @Inject
  private PermaLinkDAO permaLinkDAO;
  
  /* VectorImage */
  
  public VectorImage createVectorImage(Language language, Folder parentFolder, String title, String data, User creator) {
    String urlName = materialController.getUniqueMaterialUrlName(creator, parentFolder, null, DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));    
    return vectorImageDAO.create(creator, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }
  
	public VectorImage findVectorImageById(Long documentId) {
		return vectorImageDAO.findById(documentId);
	}

	public VectorImage updateVectorImageData(VectorImage vectorImage, String data, User modifier) {
		return vectorImageDAO.updateData(vectorImage, modifier, data);
	}

	public VectorImage updateVectorImageTitle(VectorImage vectorImage, String title, User modifier) {
	  String oldUrlName = vectorImage.getUrlName();
    String newUrlName = materialController.getUniqueMaterialUrlName(vectorImage.getCreator(), vectorImage.getParentFolder(), vectorImage, title);
    
    if (!StringUtils.equals(oldUrlName, newUrlName)) {
      String oldPath = vectorImage.getPath();
      PermaLink permaLink = permaLinkDAO.findByPath(oldPath);
      if (permaLink == null) {
        permaLink = permaLinkDAO.create(vectorImage, oldPath);
      }

      materialDAO.updateUrlName(vectorImage, newUrlName, modifier);
    }
    
    return (VectorImage) materialDAO.updateTitle(vectorImage, title, modifier);
	}

}
