package fi.foyt.fni.materials.operations;

import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;

public interface MaterialCopy <T extends Material> {

  public T copy(T original, Folder targetFolder, String urlName, User creator);
  
  public MaterialType getType();
  
  public MaterialType[] getAllowedTargets();
  
}
