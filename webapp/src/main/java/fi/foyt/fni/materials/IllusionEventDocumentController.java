package fi.foyt.fni.materials;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.IllusionEventDocumentDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class IllusionEventDocumentController {
  
  @Inject
  private IllusionEventDocumentDAO illusionEventDocumentDAO;

  public IllusionEventDocument findByFolderAndDocumentType(IllusionEventFolder folder, IllusionEventDocumentType documentType) {
    return illusionEventDocumentDAO.findByParentFolderAndDocumentType(folder, documentType);
  }

  public IllusionEventDocument createIllusionGroupDocument(User creator, IllusionEventDocumentType documentType, Language language, IllusionEventFolder parentFolder, String urlName, String title, String data, MaterialPublicity publicity) {
    return illusionEventDocumentDAO.create(creator, documentType, language, parentFolder, urlName, title, data, publicity);
  }

}
