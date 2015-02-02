package fi.foyt.fni.materials.operations;

import java.util.Date;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;

public class ImageCopy implements MaterialCopy<Image> {
  
  @Inject
  private ImageDAO imageDAO;
  
  @Override
  public Image copy(Image original, Folder targetFolder, String urlName, User creator) {
    if (original == null) {
      return null;
    }
    
    Date now = new Date();
    
    return imageDAO.create(creator,
        now,
        creator,
        now,
        original.getLanguage(),
        targetFolder, 
        urlName,
        original.getTitle(),
        original.getData(),
        original.getContentType(),
        original.getPublicity());
  }
  
  @Override
  public MaterialType getType() {
    return MaterialType.IMAGE;
  }
  
  @Override
  public MaterialType[] getAllowedTargets() {
    return new MaterialType[] {
      MaterialType.FOLDER,
      MaterialType.ILLUSION_GROUP_FOLDER
    };
  }

}
