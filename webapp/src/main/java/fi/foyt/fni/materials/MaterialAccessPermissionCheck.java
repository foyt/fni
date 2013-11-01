package fi.foyt.fni.materials;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;
import fi.foyt.fni.session.SessionController;

@Stateless
@PermissionCheck (Permission.MATERIAL_ACCESS)
public class MaterialAccessPermissionCheck implements PermissionCheckImplementation<Long> {
  
  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;

	@Override
	public boolean checkPermission(Long materialId) {
	  Material material = null;
	  if (materialId != null) {
	    material = materialController.findMaterialById(materialId);
	  }
	  
		if (material == null) {
			throw new SecurityException("Could not resolve material while checking permission for MATERIAL_ACCESS");
		}
		
		User loggedUser = sessionController.getLoggedUser();
		
		if (materialPermissionController.isPublic(loggedUser, material)) {
		  return true;
		}
		
		return materialPermissionController.hasAccessPermission(loggedUser, material);
	}

}
