package fi.foyt.fni.materials;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.dao.materials.ImageRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialRevisionSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingKeyDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageRevision;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRevision;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class ImageController {

  @Inject
  private ImageDAO imageDAO;

  @Inject
  private ImageRevisionDAO imageRevisionDAO;

  @Inject
  private MaterialDAO materialDAO;

  @Inject
  private MaterialTagDAO materialTagDAO;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialSettingKeyDAO materialSettingKeyDAO;

  @Inject
  private MaterialSettingDAO materialSettingDAO;

  @Inject
  private MaterialRevisionSettingDAO materialRevisionSettingDAO;
  
  public Image createImage(Folder parentFolder, User loggedUser, byte[] data, String contentType, String title) {
    Date now = new Date();
    String urlName = materialController.getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    return imageDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, data, contentType, MaterialPublicity.PRIVATE);
  }

  public Image findImageById(Long id) {
    return imageDAO.findById(id);
  }

  public void updateImageTitle(Image image, String title, User modifier) {
    materialDAO.updateTitle(image, title, modifier);
  }
  
  public Image updateImageContent(Image image, String contentType, byte[] data, User modifier) {
    return imageDAO.updateData(imageDAO.updateContentType(image, modifier, contentType), modifier, data);
  }
  
  /* Revisions */
  
  public ImageRevision createImageRevision(Image image, Long revisionNumber, Date created, boolean compressed, boolean completeVersion, byte[] revisionBytes, String checksum, String clientId) {
    return imageRevisionDAO.create(image, revisionNumber, created, compressed, completeVersion, revisionBytes, checksum, clientId);
  }

  public List<ImageRevision> listImageRevisionsAfter(Image image, Long revisionNumber) {
    List<ImageRevision> imageRevisions = imageRevisionDAO.listByImageAndRevisionGreaterThan(image, revisionNumber);
    Collections.sort(imageRevisions, new Comparator<ImageRevision>() {
      @Override
      public int compare(ImageRevision revision1, ImageRevision revision2) {
        return revision1.getRevision().compareTo(revision2.getRevision());
      }
    });
    
    return imageRevisions;
  }
  
  public Long getImageRevision(Image image) {
    Long result = imageRevisionDAO.maxRevisionByImage(image);
    if (result == null) {
      result = 0l;
    }
    
    return result;
  }

  /* Properties */
  
  public void setImageSetting(Image image, String key, String value) {
    MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("image." + key);
    if (settingKey != null) {
      MaterialSetting materialSetting = materialSettingDAO.findByMaterialAndKey(image, settingKey);
      if (materialSetting != null) {
        materialSettingDAO.updateValue(materialSetting, value);
      } else {
        materialSettingDAO.create(image, settingKey, value);
      }
    }
  }
  
  /* Tags */

  public List<MaterialTag> listImageTags(Image image) {
    return materialTagDAO.listByMaterial(image);
  }
  
  public Image setImageTags(Image image, List<Tag> tags) {
    List<MaterialTag> removeTags = null;
    if (tags.size() > 0) {
      removeTags = materialTagDAO.listByMaterialAndTagsNotIn(image, tags);
    } else {
      removeTags = materialTagDAO.listByMaterial(image);
    }
    
    for (MaterialTag removeTag : removeTags) {
      materialTagDAO.delete(removeTag);
    }
    
    for (Tag tag : tags) {
      if (materialTagDAO.findByMaterialAndTag(image, tag) == null) {
        materialTagDAO.create(image, tag);
      }
    }
    
    return image;
  }

  /* Revision Settings */

  public MaterialRevisionSetting createImageRevisionSetting(MaterialRevision materialRevision, String key, String value) {
    MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("image." + key);
    if (settingKey != null) {
      return materialRevisionSettingDAO.create(materialRevision, settingKey, value);
    }
    
    return null;
  }

  public List<MaterialRevisionSetting> listImageRevisionSettings(ImageRevision imageRevision) {
    return materialRevisionSettingDAO.listByMaterialRevision(imageRevision);
  }
}
