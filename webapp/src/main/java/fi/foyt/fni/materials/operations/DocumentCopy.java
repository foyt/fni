package fi.foyt.fni.materials.operations;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;

public class DocumentCopy implements MaterialCopy<Document> {
  
  @Inject
  private DocumentDAO documentDAO;
  
  @Override
  public Document copy(Document original, Folder targetFolder, String urlName, User creator) {
    if (original == null) {
      return null;
    }
    
    return documentDAO.create(creator,
        original.getLanguage(),
        targetFolder, 
        urlName,
        original.getTitle(),
        original.getData(),
        original.getPublicity());
  }
  
  @Override
  public MaterialType getType() {
    return MaterialType.DOCUMENT;
  }
  
  @Override
  public MaterialType[] getAllowedTargets() {
    return new MaterialType[] {
      MaterialType.FOLDER,
      MaterialType.ILLUSION_GROUP_FOLDER
    };
  }

}
