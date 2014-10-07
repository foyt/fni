package fi.foyt.fni.illusion;

import java.util.List;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.illusion.IllusionEventMaterialParticipantSettingDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class IllusionEventMaterialController {

  @Inject
  private IllusionEventMaterialParticipantSettingDAO illusionEventMaterialParticipantSettingDAO;
  
  public IllusionEventMaterialParticipantSetting createParticipantSetting(Material material, IllusionEventParticipant participant, IllusionEventMaterialParticipantSettingKey key, String value) {
    return illusionEventMaterialParticipantSettingDAO.create(material, participant, key, value);
  }

  public IllusionEventMaterialParticipantSetting findParticipantSettingById(Long id) {
    return illusionEventMaterialParticipantSettingDAO.findById(id);
  }
  
  public IllusionEventMaterialParticipantSetting findParticipantSettingByMaterialAndParticipantAndKey(Material material, IllusionEventParticipant participant, IllusionEventMaterialParticipantSettingKey key) {
    return illusionEventMaterialParticipantSettingDAO.findByMaterialAndParticipantAndKey(material, participant, key); 
  }

  public List<IllusionEventMaterialParticipantSetting> listParticipantSettingByMaterialAndParticipant(Material material, IllusionEventParticipant participant) {
    return illusionEventMaterialParticipantSettingDAO.listByMaterialAndParticipantAndKey(material, participant); 
  }
  
  public IllusionEventMaterialParticipantSetting updateParticipantSettingValue(IllusionEventMaterialParticipantSetting participantSetting, String value) {
    return illusionEventMaterialParticipantSettingDAO.updateValue(participantSetting, value);
  }
  
  /* EventFolder */

  public IllusionEventFolder getIllusionEventFolder(Material material) {
    Folder parent = material.getParentFolder();
    while (parent != null) {
      if (parent.getType() == MaterialType.ILLUSION_GROUP_FOLDER) {
        return (IllusionEventFolder) parent;
      }

      parent = parent.getParentFolder();
    }

    return null;
  }
  
}
