package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;
import fi.foyt.fni.session.SessionController;

@Stateless
@PermissionCheck (Permission.ILLUSION_EVENT_ACCESS)
public class IllusionEventAccessPermissionCheck implements PermissionCheckImplementation<String> {
  
  private static final IllusionEventParticipantRole[] DEFAULT_ROLES = {
    IllusionEventParticipantRole.ORGANIZER, IllusionEventParticipantRole.PARTICIPANT
  };

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;
  
	@Override
	public boolean checkPermission(String illusionEventUrlName, Map<String, String> parameters) {
	  if (sessionController.isLoggedIn()) {
      IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(illusionEventUrlName);
      if (illusionEvent == null) {
        throw new SecurityException("Could not resolve Illusion group while checking permission for ILLUSION_GROUP_ACCESS");
      }
      
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, sessionController.getLoggedUser());
      if (participant == null) { 
        return false;
      }
      

      List<IllusionEventParticipantRole> roles = new ArrayList<>();
      if (parameters.containsKey("roles")) {
        for (String roleString : parameters.get("roles").split(",")) {
          roles.add(IllusionEventParticipantRole.valueOf(roleString));
        }
      } else {
        roles.addAll(Arrays.asList(DEFAULT_ROLES));
      }
      
      for (IllusionEventParticipantRole role : roles) {
        if (role == participant.getRole()) {
          return true;
        }
      }
      
      return false;
    }
    
    return false;
	}

}
