package fi.foyt.fni.materials;

import java.util.List;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.UserMaterialRoleDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

public class MaterialPermissionController {
	
	@Inject
	private UserController userController;

	@Inject
	private UserMaterialRoleDAO userMaterialRoleDAO;

	public boolean isOwner(User user, Material material) {

		if (user == null)
			return false;
		
		return material.getCreator().getId().equals(user.getId());
	}
	
	public boolean isPublic(User user, Material material) {
		if (material.getPublicity() == MaterialPublicity.PRIVATE)
			return false;
		
		if (material.getPublicity() == MaterialPublicity.PUBLIC || material.getPublicity() == MaterialPublicity.LINK)
			return true;
		
		if (material.getPublicity() == MaterialPublicity.FRIENDS) {
			if (user == null)
				return false;
			
			if (user.getId().equals(material.getCreator().getId()))
				return true;
			
			if (userController.areFriends(user, material.getCreator()))
				return true;
			
			List<UserMaterialRole> materialEditors = userMaterialRoleDAO.listByMaterialAndRole(material, MaterialRole.MAY_EDIT);
			for (UserMaterialRole userMaterialRole : materialEditors) {
				if (user.getId().equals(userMaterialRole.getUser().getId()))
				  return true;
				
				if (userController.areFriends(user, userMaterialRole.getUser()))
					return true;
			}
		}
		
		return false;
	}
	
	public boolean hasAccessPermission(User user, Material material) {
		if (user == null)
			return false;
		
  	if (material.getCreator().getId().equals(user.getId()))
  		return true;
  	
  	UserMaterialRole role = userMaterialRoleDAO.findByMaterialAndUser(material, user);
  	if (role == null) {
  		if (material.getParentFolder() != null) {
  			return hasAccessPermission(user, material.getParentFolder());
  		} else {
  			return false;
  		}
  	}
  	
  	if (role.getRole() == MaterialRole.MAY_EDIT)
  		return true;
  	
  	if (role.getRole() == MaterialRole.MAY_VIEW)
  		return true;
  	
  	return false;
	}
	
	public boolean hasModifyPermission(User user, Material material) {
		if (user == null)
			return false;
		
		if (material.getCreator().getId().equals(user.getId()))
  		return true;
		
  	UserMaterialRole role = userMaterialRoleDAO.findByMaterialAndUser(material, user);
  	if (role == null) {
  		if (material.getParentFolder() != null) {
  			return hasModifyPermission(user, material.getParentFolder());
  		} else {
  			return false;
  		}
  	}
  	
  	if (role.getRole() == MaterialRole.MAY_EDIT)
  		return true;
  	
  	return false;
	}
	
	public MaterialRole getUserMaterialRole(User user, Material material) {
		UserMaterialRole userMaterialRole = userMaterialRoleDAO.findByMaterialAndUser(material, user);
		if (userMaterialRole != null) {
			return userMaterialRole.getRole();
		}
		
		return null;
	}
	
}
