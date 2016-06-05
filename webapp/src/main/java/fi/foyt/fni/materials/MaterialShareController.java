package fi.foyt.fni.materials;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.MaterialShareGroupDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialShareUserDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserGroup;

@Dependent
public class MaterialShareController {
	
  @Inject
  private MaterialShareUserDAO materialShareUserDAO;
  
  @Inject
  private MaterialShareGroupDAO materialShareGroupDAO;

  public MaterialShareUser createMaterialShareUser(User user, Material material, MaterialRole role) {
    return materialShareUserDAO.create(material, user, role);
  }
  
  public MaterialShareUser findMaterialShareUser(Long id) {
    return materialShareUserDAO.findById(id);
  }

  public MaterialShareUser findMaterialShareUserByUserAndMaterial(User user, Material material) {
    return materialShareUserDAO.findByMaterialAndUser(material, user);
  }
  
  public List<MaterialShareUser> listMaterialSharesUsers(Material material) {
    return materialShareUserDAO.listByMaterial(material);
  }
  
  public MaterialShareUser updateMaterialShareUser(MaterialShareUser materialShareUser, MaterialRole role) {
    return materialShareUserDAO.updateRole(materialShareUser, role);
  }

  public void deleteMaterialShareUser(MaterialShareUser materialShareUser) {
    materialShareUserDAO.delete(materialShareUser);
  }

  public MaterialShareGroup createMaterialShareGroup(UserGroup userGroup, Material material, MaterialRole role) {
    return materialShareGroupDAO.create(material, userGroup, role);
  }

  public MaterialShareGroup findMaterialShareGroup(Long id) {
    return materialShareGroupDAO.findById(id);
  }

  public MaterialShareGroup findMaterialShareGroupByGroupAndMaterial(UserGroup userGroup, Material material) {
    return materialShareGroupDAO.findByMaterialAndUserGroup(material, userGroup);
  }

  public List<MaterialShareGroup> listMaterialSharesGroups(Material material) {
    return materialShareGroupDAO.listByMaterial(material);
  }

  public MaterialShareGroup updateMaterialShareGroup(MaterialShareGroup materialShareGroup, MaterialRole role) {
    return materialShareGroupDAO.updateRole(materialShareGroup, role);
  }

  public void deleteMaterialShareGroup(MaterialShareGroup materialShareGroup) {
    materialShareGroupDAO.delete(materialShareGroup);
  }

}
