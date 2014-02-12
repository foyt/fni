package fi.foyt.fni.illusion;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;
import fi.foyt.fni.session.SessionController;

@Stateless
@PermissionCheck (Permission.ILLUSION_GROUP_ACCESS)
public class IllusionGroupAccessPermissionCheck implements PermissionCheckImplementation<Long> {

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionGroupController illusionGroupController;
  
	@Override
	public boolean checkPermission(Long illusionGroupId, Map<String, Object> parameters) {
	  if (sessionController.isLoggedIn()) {
      IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupById(illusionGroupId);
      if (illusionGroup == null) {
        throw new SecurityException("Could not resolve Illusion group while checking permission for ILLUSION_GROUP_ACCESS");
      }
      
      IllusionGroupUser illusionGroupUser = illusionGroupController.findIllusionGroupUserByUserAndGroup(illusionGroup, sessionController.getLoggedUser());
      return illusionGroupUser != null;
    }
    
    return false;
	}

}
