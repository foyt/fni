package fi.foyt.fni.materials;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.UserMaterialRoleDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class MaterialUserController {
	
	@Inject
	private UserMaterialRoleDAO userMaterialRoleDAO;

  public List<UserMaterialRole> listMaterialUsers(Material material) {
    return userMaterialRoleDAO.listByMaterial(material);
  }
  
  public UserMaterialRole createUserMaterialRole(User user, Material material, MaterialRole role) {
    return userMaterialRoleDAO.create(material, user, role);
  }
  
  public UserMaterialRole findUserMaterialRole(User user, Material material) {
    return userMaterialRoleDAO.findByMaterialAndUser(material, user);
  }

  public UserMaterialRole updateUserMaterialRole(UserMaterialRole userMaterialRole, MaterialRole role) {
    return userMaterialRoleDAO.updateRole(userMaterialRole, role);
  }

  public void deleteUserMaterialRole(UserMaterialRole userMaterialRole) {
    userMaterialRoleDAO.delete(userMaterialRole);
  }
  
  public UserMaterialRole setMaterialUserRole(User user, Material material, MaterialRole role) {
    UserMaterialRole userMaterialRole = findUserMaterialRole(user, material);
    if (userMaterialRole != null) {
      if (role != null) {
        return updateUserMaterialRole(userMaterialRole, role);
      } else {
        deleteUserMaterialRole(userMaterialRole);
      }
    } else {
      if (role != null) {
        return createUserMaterialRole(user, material, role); 
      }
    }

    return null;
  }
  
}
