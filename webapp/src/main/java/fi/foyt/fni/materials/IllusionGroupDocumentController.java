package fi.foyt.fni.materials;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.IllusionGroupDocumentDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class IllusionGroupDocumentController {
  
  @Inject
  private IllusionGroupDocumentDAO illusionGroupDocumentDAO;

  public IllusionGroupDocument findByFolderAndDocumentType(IllusionEventFolder folder, IllusionEventDocumentType documentType) {
    return illusionGroupDocumentDAO.findByParentFolderAndDocumentType(folder, documentType);
  }

  public IllusionGroupDocument createIllusionGroupDocument(User creator, IllusionEventDocumentType documentType, Language language, IllusionEventFolder parentFolder, String urlName, String title, String data, MaterialPublicity publicity) {
    return illusionGroupDocumentDAO.create(creator, documentType, language, parentFolder, urlName, title, data, publicity);
  }

}
