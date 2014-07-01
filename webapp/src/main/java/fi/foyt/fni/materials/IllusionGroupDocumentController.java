package fi.foyt.fni.materials;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.IllusionGroupDocumentDAO;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;

@Dependent
@Stateless
public class IllusionGroupDocumentController {
  
  @Inject
  private IllusionGroupDocumentDAO illusionGroupDocumentDAO;

  public IllusionGroupDocument findByFolderAndDocumentType(IllusionGroupFolder folder, IllusionGroupDocumentType documentType) {
    return illusionGroupDocumentDAO.findByParentFolderAndDocumentType(folder, documentType);
  }

}
