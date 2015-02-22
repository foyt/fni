package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/character-sheets/{ownerId}/{urlPath}", to = "/forge/character-sheets.jsf")
@LoggedIn
public class ForgeCharacterSheetsBackingBean {
  
  @Parameter
  @Matches ("[0-9]{1,}")
  private Long ownerId;
  
  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String urlPath;
  
	@Inject
	private SessionController sessionController;
	
	@Inject
	private MaterialController materialController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private NavigationController navigationController;
	
	@RequestAction
	@Deferred
	public String load() throws FileNotFoundException {
		if ((getOwnerId() == null)||(getUrlPath() == null)) {
			return navigationController.notFound();
		}
		
		String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
		Material material = materialController.findMaterialByCompletePath(completePath);
		User loggedUser = sessionController.getLoggedUser();
		
		if (!(material instanceof CharacterSheet)) {
      return navigationController.notFound();
		}

    CharacterSheet characterSheet = (CharacterSheet) material;
    
		if (!materialPermissionController.hasAccessPermission(loggedUser, characterSheet)) {
      return navigationController.accessDenied();
		}
		
		readOnly = !materialPermissionController.hasModifyPermission(loggedUser, material);
		materialId = characterSheet.getId();
		characterSheetTitle = characterSheet.getTitle();
		characterSheetContents = characterSheet.getContents();
		characterSheetStyles = characterSheet.getStyles();
		characterSheetScripts = characterSheet.getScripts();
		
		folders = ForgeViewUtils.getParentList(material);
		
		return null;
	}
	
	public Long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getUrlPath() {
		return urlPath;
	}
	
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	
	public Long getMaterialId() {
		return materialId;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	
	public String getCharacterSheetTitle() {
    return characterSheetTitle;
  }
	
	public void setCharacterSheetTitle(String characterSheetTitle) {
    this.characterSheetTitle = characterSheetTitle;
  }
	
	public String getCharacterSheetContents() {
    return characterSheetContents;
  }
	
	public void setCharacterSheetContents(String characterSheetContents) {
    this.characterSheetContents = characterSheetContents;
  }
	
	public String getCharacterSheetScripts() {
    return characterSheetScripts;
  }
	
	public void setCharacterSheetScripts(String characterSheetScripts) {
    this.characterSheetScripts = characterSheetScripts;
  }
	
	public String getCharacterSheetStyles() {
    return characterSheetStyles;
  }
	
	public void setCharacterSheetStyles(String characterSheetStyles) {
    this.characterSheetStyles = characterSheetStyles;
  }
	
	public boolean isReadOnly() {
    return readOnly;
  }
	
	public String save() {
    User loggedUser = sessionController.getLoggedUser();
    CharacterSheet characterSheet = materialController.findCharacterSheetById(materialId);
    
	  if (materialPermissionController.hasModifyPermission(loggedUser, characterSheet)) {
	    materialController.updateCharacterSheet(characterSheet, getCharacterSheetTitle(), 
        getCharacterSheetContents(), getCharacterSheetStyles(), getCharacterSheetScripts(), loggedUser);

      String ownerId = characterSheet.getCreator().getId().toString();
	    String urlPath = characterSheet.getPath().substring(ownerId.length() + 1);
	    
	    return "/forge/character-sheets.jsf?faces-redirect=true&ownerId=" + ownerId + "&urlPath=" + urlPath;
	  } else {
      return navigationController.accessDenied();
	  }
	}
	
	private Long materialId;
	private String characterSheetTitle;
  private String characterSheetContents;
  private String characterSheetScripts;
	private String characterSheetStyles;
	private List<Folder> folders;
	private boolean readOnly;
}
