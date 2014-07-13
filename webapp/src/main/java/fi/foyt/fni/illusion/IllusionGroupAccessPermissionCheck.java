package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;
import fi.foyt.fni.session.SessionController;

@Stateless
@PermissionCheck (Permission.ILLUSION_GROUP_ACCESS)
public class IllusionGroupAccessPermissionCheck implements PermissionCheckImplementation<String> {
  
  private static final IllusionGroupMemberRole[] DEFAULT_ROLES = {
    IllusionGroupMemberRole.GAMEMASTER, IllusionGroupMemberRole.PLAYER
  };

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionGroupController illusionGroupController;
  
	@Override
	public boolean checkPermission(String illusionGroupUrlName, Map<String, String> parameters) {
	  if (sessionController.isLoggedIn()) {
      IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(illusionGroupUrlName);
      if (illusionGroup == null) {
        throw new SecurityException("Could not resolve Illusion group while checking permission for ILLUSION_GROUP_ACCESS");
      }
      
      IllusionGroupMember illusionGroupUser = illusionGroupController.findIllusionGroupMemberByUserAndGroup(illusionGroup, sessionController.getLoggedUser());
      if (illusionGroupUser == null) { 
        return false;
      }
      

      List<IllusionGroupMemberRole> roles = new ArrayList<>();
      if (parameters.containsKey("roles")) {
        for (String roleString : parameters.get("roles").split(",")) {
          roles.add(IllusionGroupMemberRole.valueOf(roleString));
        }
      } else {
        roles.addAll(Arrays.asList(DEFAULT_ROLES));
      }
      
      for (IllusionGroupMemberRole role : roles) {
        if (role == illusionGroupUser.getRole()) {
          return true;
        }
      }
      
      return false;
    }
    
    return false;
	}

}
