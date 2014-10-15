package fi.foyt.fni.illusion;

import java.io.FileNotFoundException;
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
@PermissionCheck (Permission.ILLUSION_EVENT_MANAGE)
public class IllusionEventManagePermissionCheck implements PermissionCheckImplementation<String> {
  
  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;
  
	@Override
	public boolean checkPermission(String illusionEventUrlName, Map<String, String> parameters) throws FileNotFoundException {
	  if (sessionController.isLoggedIn()) {
      if (illusionEventUrlName == null) {
        throw new SecurityException("Could not resolve Illusion event while checking permission for ILLUSION_EVENT_MANAGE");
      }
	    
	    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(illusionEventUrlName);
      if (illusionEvent == null) {
        throw new FileNotFoundException();
      }
      
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, sessionController.getLoggedUser());
      if (participant == null) { 
        return false;
      }
      
      return participant.getRole() == IllusionEventParticipantRole.ORGANIZER;
    }
    
    return false;
	}

}
