package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.security.SecurityParam;
import fi.foyt.fni.security.SecurityParams;
import fi.foyt.fni.utils.servlet.RequestUtils;
import fi.foyt.fni.view.illusion.IllusionEventNavigationController.SelectedPage;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/settings", to = "/illusion/event-settings.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_ACCESS, deferred = true)
@SecurityContext (context = "@urlName")
@SecurityParams ({
  @SecurityParam (name = "roles", value = "ORGANIZER")
})
public class IllusionEventSettingsBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      return "/error/access-denied.jsf";
    }

    illusionEventNavigationController.setSelectedPage(SelectedPage.SETTINGS);
    illusionEventNavigationController.setEventUrlName(getUrlName());

    name = illusionEvent.getName();
    description = illusionEvent.getDescription();
    joinMode = illusionEvent.getJoinMode();
    
    return null;
  }
  
	public String getName() {
    return name;
  }
	
	public void setName(String name) {
    this.name = name;
  }
	
	public String getDescription() {
    return description;
  }
	
	public void setDescription(String description) {
    this.description = description;
  }
	
	public IllusionEventJoinMode getJoinMode() {
    return joinMode;
  }
	
	public void setJoinMode(IllusionEventJoinMode joinMode) {
    this.joinMode = joinMode;
  }
	
	@Override
	public String getUrlName() {
	  return urlName;
	}
	
	public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
	
	private String createUrlName(String name) {
    int maxLength = 20;
    int padding = 0;
    do {
      String urlName = RequestUtils.createUrlName(name, maxLength);
      if (padding > 0) {
        urlName = urlName.concat(StringUtils.repeat('_', padding));
      }
      
      IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(urlName);
      if (illusionEvent == null) {
        return urlName;
      }
      
      if (maxLength < name.length()) {
        maxLength++;
      } else {
        padding++;
      }
    } while (true);
  }
	
  public String save() throws Exception {
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(getUrlName());
    if (!illusionEvent.getName().equals(getName())) {
      String urlName = createUrlName(getName());
      illusionEventController.updateIllusionEventName(illusionEvent, getName());
      illusionEventController.updateIllusionEventUrlName(illusionEvent, urlName);
    }

    illusionEventController.updateIllusionEventDescription(illusionEvent, getDescription()); 
    illusionEventController.updateIllusionEventJoinMode(illusionEvent, getJoinMode());
    
    return "/illusion/event-settings.jsf?faces-redirect=true&urlName=" + illusionEvent.getUrlName();
  }
	
	private String name;
	private String description;
	private IllusionEventJoinMode joinMode;
}
