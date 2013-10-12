package fi.foyt.fni.view.forge;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { @URLMapping(id = "forge-index", pattern = "/forge/", viewId = "/forge/index.jsf") })
public class ForgeIndexBackingBean {

	private static final int MAX_LAST_VIEWED_MATERIALS = 5;
	private static final int MAX_LAST_EDITED_MATERIALS = 5;

	@Inject
	private MaterialController materialController;

	@Inject
	private SessionController sessionController;

	@PostConstruct
	public void init() {
	}

	@URLAction
	@LoggedIn
	public void load() {
		folderId = null;
		materialsOpen = true;
		lastViewedOpen = true;
		starredOpen = true;
		lastEditedOpen = true;
	}

	public List<Material> getMaterials() {
		Folder folder = null;

		List<Material> materials = materialController.listMaterialsByFolder(sessionController.getLoggedUser(), folder);
		Collections.sort(materials, new TitleComparator());

		return materials;
	}

	public List<Material> getLastViewedMaterials() {
		return materialController.listViewedMaterialsByUser(sessionController.getLoggedUser(), 0, MAX_LAST_VIEWED_MATERIALS);
	}

	public List<Material> getLastEditedMaterials() {
		return materialController.listModifiedMaterialsByUser(sessionController.getLoggedUser(), 0, MAX_LAST_EDITED_MATERIALS);
	}

	public List<Material> getStarredMaterials() {
		return materialController.listStarredMaterialsByUser(sessionController.getLoggedUser());
	}

	public boolean isStarred(Long materialId) {
		Material material = materialController.findMaterialById(materialId);
		if (material != null) {
			return materialController.isStarred(sessionController.getLoggedUser(), material);
		}

		return false;
	}

	public void starMaterial(Long materialId) {
		Material material = materialController.findMaterialById(materialId);
		if (material != null) {
			User loggedUser = sessionController.getLoggedUser();
			materialController.starMaterial(material, loggedUser);
		}
	}

	public void unstarMaterial(Long materialId) {
		Material material = materialController.findMaterialById(materialId);
		if (material != null) {
			User loggedUser = sessionController.getLoggedUser();
			materialController.unstarMaterial(material, loggedUser);
		}
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public boolean isMaterialsOpen() {
		return materialsOpen;
	}

	public void setMaterialsOpen(boolean materialsOpen) {
		this.materialsOpen = materialsOpen;
	}

	public boolean isLastViewedOpen() {
		return lastViewedOpen;
	}

	public void setLastViewedOpen(boolean lastViewedOpen) {
		this.lastViewedOpen = lastViewedOpen;
	}

	public boolean isStarredOpen() {
		return starredOpen;
	}

	public void setStarredOpen(boolean starredOpen) {
		this.starredOpen = starredOpen;
	}

	public boolean isLastEditedOpen() {
		return lastEditedOpen;
	}

	public void setLastEditedOpen(boolean lastEditedOpen) {
		this.lastEditedOpen = lastEditedOpen;
	}

	public void closeMaterialsList() {
		setMaterialsOpen(false);
	}

	public void openMaterialsList() {
		setMaterialsOpen(true);
	}

	public void closeLastViewedList() {
		setLastViewedOpen(false);
	}

	public void openLastViewedList() {
		setLastViewedOpen(true);
	}

	public void closeLastEditedList() {
		setLastEditedOpen(false);
	}

	public void openLastEditedList() {
		setLastEditedOpen(true);
	}

	public void closeStarredList() {
		setStarredOpen(false);
	}

	public void openStarredList() {
		setStarredOpen(true);
	}

	private Long folderId;
	private boolean materialsOpen;
	private boolean lastViewedOpen;
	private boolean starredOpen;
	private boolean lastEditedOpen;
}
