package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class CharacterSheetDAO extends GenericDAO<CharacterSheet> {

	private static final long serialVersionUID = 1L;

	public CharacterSheet create(Folder parentFolder, String contents, String styles, String scripts, Language language, String title, String urlName, MaterialPublicity publicity, User creator, Date created, User modifier, Date modified) {
    CharacterSheet characterSheet = new CharacterSheet();
    characterSheet.setContents(contents);
    characterSheet.setScripts(scripts);
    characterSheet.setStyles(styles);
    characterSheet.setLanguage(language);
    characterSheet.setCreated(created);
    characterSheet.setCreator(creator);
    characterSheet.setModified(modified);
    characterSheet.setModifier(modifier);
    characterSheet.setTitle(title);
    characterSheet.setUrlName(urlName);
    characterSheet.setPublicity(publicity);
    characterSheet.setParentFolder(parentFolder);
    
    return persist(characterSheet);
  }

  public CharacterSheet updateTitle(CharacterSheet characterSheet, String title) {
    characterSheet.setTitle(title);
    return persist(characterSheet);
  }

  public CharacterSheet updateContents(CharacterSheet characterSheet, String contents) {
    characterSheet.setContents(contents);
    return persist(characterSheet);
  }

  public CharacterSheet updateStyles(CharacterSheet characterSheet, String styles) {
    characterSheet.setStyles(styles);
    return persist(characterSheet);
  }

  public CharacterSheet updateScripts(CharacterSheet characterSheet, String scripts) {
    characterSheet.setScripts(scripts);
    return persist(characterSheet);
  }

  public CharacterSheet updateModifier(CharacterSheet characterSheet, User modifier) {
    characterSheet.setModifier(modifier);
    return persist(characterSheet);
  }

  public CharacterSheet updateModified(CharacterSheet characterSheet, Date modified) {
    characterSheet.setModified(modified);
    return persist(characterSheet);
  }

  public CharacterSheet updateUrlName(CharacterSheet characterSheet, String urlName) {
    characterSheet.setUrlName(urlName);
    return persist(characterSheet);
  }

}
