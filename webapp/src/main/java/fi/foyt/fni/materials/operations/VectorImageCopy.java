package fi.foyt.fni.materials.operations;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;

public class VectorImageCopy implements MaterialCopy<VectorImage> {
  
  @Inject
  private VectorImageDAO vectorImageDAO;
  
  @Override
  public VectorImage copy(VectorImage original, Folder targetFolder, String urlName, User creator) {
    if (original == null) {
      return null;
    }
    
    return vectorImageDAO.create(creator,
      original.getLanguage(),
      targetFolder, 
      urlName,
      original.getTitle(),
      original.getData(),
      original.getPublicity());
  }
  
  @Override
  public MaterialType getType() {
    return MaterialType.VECTOR_IMAGE;
  }
  
  @Override
  public MaterialType[] getAllowedTargets() {
    return new MaterialType[] {
      MaterialType.FOLDER,
      MaterialType.ILLUSION_GROUP_FOLDER
    };
  }

}
