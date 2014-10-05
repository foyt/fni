package fi.foyt.fni.materials;

import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.materials.CharacterSheetDAO;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class CharacterSheetMaterialController {
	
  @Inject
	private CharacterSheetDAO characterSheetDAO;

  @Inject
  private MaterialController materialController;

	public CharacterSheet createCharacterSheet(Folder parentFolder, String title, String content, User creator, String styles, String scripts) {
	  Date now = new Date();
    String urlName = materialController.getUniqueMaterialUrlName(creator, parentFolder, null, title);
	  return characterSheetDAO.create(parentFolder, content, styles, scripts, null, title, urlName, MaterialPublicity.PRIVATE, creator, now, creator, now);
	}
	
	public CharacterSheet findCharacterSheetById(Long id) {
	  return characterSheetDAO.findById(id);
	}
	
	public CharacterSheet updateCharacterSheet(CharacterSheet characterSheet, String title, String contents, String styles, String scripts, User modifier) {
	  if (!StringUtils.equals(characterSheet.getTitle(), title)) {
	    characterSheetDAO.updateTitle(characterSheet, title);
	    String urlName = materialController.getUniqueMaterialUrlName(characterSheet.getCreator(), characterSheet.getParentFolder(), characterSheet, title);
	    characterSheetDAO.updateUrlName(characterSheet, urlName);
	  }
	  
	  characterSheetDAO.updateContents(characterSheet, contents);
    characterSheetDAO.updateStyles(characterSheet, styles);
    characterSheetDAO.updateScripts(characterSheet, scripts);
    characterSheetDAO.updateModifier(characterSheet, modifier);
    characterSheetDAO.updateModified(characterSheet, new Date());
    return characterSheet;
	}
	
}
