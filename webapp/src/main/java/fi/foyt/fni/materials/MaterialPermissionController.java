package fi.foyt.fni.materials;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.MaterialShareGroupDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialShareUserDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser;
import fi.foyt.fni.persistence.model.users.User;

public class MaterialPermissionController {
	
  @Inject
  private MaterialShareUserDAO materialShareUserDAO;
  
  @Inject
  private MaterialShareGroupDAO materialShareGroupDAO;

	public boolean isOwner(User user, Material material) {

		if (user == null)
			return false;
		
		return material.getCreator().getId().equals(user.getId());
	}
	
	public boolean isPublic(User user, Material material) {
		if (material.getPublicity() == MaterialPublicity.PRIVATE)
			return false;
		
		if (material.getPublicity() == MaterialPublicity.PUBLIC || material.getPublicity() == MaterialPublicity.LINK) {
			return true;
		}
		
		return false;
	}
	
	public boolean hasAccessPermission(User user, Material material) {
		if (user == null) {
			return false;
		}
		
  	if (material.getCreator().getId().equals(user.getId())) {
  		return true;
  	}
  	
  	MaterialRole materialRole = getUserMaterialRole(user, material);
  	if (materialRole == null) {
  		if (material.getParentFolder() != null) {
  			return hasAccessPermission(user, material.getParentFolder());
  		} else {
  			return false;
  		}
  	}
  	
  	if (materialRole == MaterialRole.MAY_EDIT) {
  		return true;
  	}
  	
  	if (materialRole == MaterialRole.MAY_VIEW) {
  		return true;
  	}
  	
  	return false;
	}
	
	public boolean hasModifyPermission(User user, Material material) {
		if (user == null)
			return false;
		
		if (material.getCreator().getId().equals(user.getId())) {
  		return true;
		}
		
    MaterialRole materialRole = getUserMaterialRole(user, material);
  	if (materialRole == null) {
  		if (material.getParentFolder() != null) {
  			return hasModifyPermission(user, material.getParentFolder());
  		} else {
  			return false;
  		}
  	}
  	
  	if (materialRole == MaterialRole.MAY_EDIT) {
  		return true;
  	}
  	
  	return false;
	}
	
	public MaterialRole getUserMaterialRole(User user, Material material) {
	  MaterialShareUser materialShareUser = materialShareUserDAO.findByMaterialAndUser(material, user);
    if (materialShareUser != null) {
      return materialShareUser.getRole();
    }

    MaterialShareGroup materialShareGroup = materialShareGroupDAO.findByMaterialAndUser(material, user);
    if (materialShareGroup != null) {
      return materialShareGroup.getRole();
    }
		
		return null;
	}
	
}
