package fi.foyt.fni.materials.operations;

import java.util.Date;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.CharacterSheetDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;

public class CharacterSheetMaterialCopy implements MaterialCopy<CharacterSheet> {
  
  @Inject
  private CharacterSheetDAO characterSheetDAO;
  
  @Override
  public CharacterSheet copy(CharacterSheet original, Folder targetFolder, String urlName, User creator) {
    if (original == null) {
      return null;
    }
    
    Date now = new Date();
    
    CharacterSheet characterSheet = characterSheetDAO.create(targetFolder, 
        original.getContents(), 
        original.getStyles(), 
        original.getScripts(), 
        original.getLanguage(),
        original.getTitle(), 
        urlName, 
        original.getPublicity(), 
        creator, 
        now, 
        creator, 
        now);
    
    return characterSheet;
  }
  
  @Override
  public MaterialType getType() {
    return MaterialType.CHARACTER_SHEET;
  }
  
  @Override
  public MaterialType[] getAllowedTargets() {
    return new MaterialType[] {
      MaterialType.ILLUSION_GROUP_FOLDER
    };
  }

}
