package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;

public class ForgeViewUtils {

	public static List<Folder> getParentList(Material material) {
		List<Folder> folders = new ArrayList<>();
		
		Folder folder = material.getParentFolder();
		while (folder != null) {
		  folders.add(0, folder);
		  folder = folder.getParentFolder();
		};
		
		return folders;
	}
	
}
