package fi.foyt.fni.materials;

import java.util.Comparator;

import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;

public class MaterialTypeComparator implements Comparator<Material> {
  
  public MaterialTypeComparator(MaterialType type) {
    this.type = type;
  }
  
  @Override
  public int compare(Material o1, Material o2) {
    if (o1.getType() == o2.getType())
      return 0;
    
    if (o1.getType() == type)
      return -1;
    
    if (o2.getType() == type)
      return 1;

    return 0;
  }
  
  private MaterialType type;
}