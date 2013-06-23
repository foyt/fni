package fi.foyt.fni.materials;

import java.util.Comparator;

import fi.foyt.fni.persistence.model.materials.Material;

public class TitleComparator implements Comparator<Material> {

	@Override
  public int compare(Material material1, Material material2) {
		return material1.getTitle().compareTo(material2.getTitle());
  }

}
