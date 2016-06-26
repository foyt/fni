package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/participants", to = "/illusion/event-participants.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext (context = "@urlName")
public class IllusionEventParticipantsBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private NavigationController navigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      return navigationController.accessDenied();
    }

    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.PARTICIPANTS);
    illusionEventNavigationController.setEventUrlName(getUrlName());

    organizers = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.ORGANIZER);
    participants = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.PARTICIPANT);
    banned = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.BANNED);
    eventJoinMode = illusionEvent.getJoinMode();
    if (eventJoinMode == IllusionEventJoinMode.APPROVE) {
      approvalPending = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.PENDING_APPROVAL);
    }
    
    waitingPayment = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.WAITING_PAYMENT);
    invited = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.INVITED);
    
    String eventUrl = illusionEventController.getEventUrl(illusionEvent);
    this.joinUrl = eventUrl + "/dojoin?ref=inv";
    this.eventUrl = eventUrl + "?ref=inv";
    
    return null;
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  public void setDefaults() {
    selectParticipant(approvalPending != null && !approvalPending.isEmpty() ? approvalPending.get(0) : !participants.isEmpty() ? participants.get(0) : organizers.get(0));
  }
  
  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public List<IllusionEventParticipant> getOrganizers() {
    return organizers;
  }
  
  public List<IllusionEventParticipant> getParticipants() {
    return participants;
  }

  public List<IllusionEventParticipant> getApprovalPending() {
    return approvalPending;
  }
  
  public List<IllusionEventParticipant> getBanned() {
    return banned;
  }
  
  public List<IllusionEventParticipant> getWaitingPayment() {
    return waitingPayment;
  }
  
  public List<IllusionEventParticipant> getInvited() {
    return invited;
  }
  
  public IllusionEventJoinMode getEventJoinMode() {
    return eventJoinMode;
  }
  
  public String getJoinUrl() {
    return joinUrl;
  }
  
  public String getEventUrl() {
    return eventUrl;
  }
  
  public void selectParticipant(IllusionEventParticipant participant) {
    selectedParticipantId = participant.getId();
    selectedParticipantUserId = participant.getUser().getId();
    selectedParticipantName = getParticipantDisplayName(participant);
    selectedParticipantDisplayName = participant.getDisplayName();
    selectedParticipantRole = participant.getRole();
  }

  public Long getSelectedParticipantId() {
    return selectedParticipantId;
  }
  
  public void setSelectedParticipantId(Long selectedParticipantId) {
    this.selectedParticipantId = selectedParticipantId;
  }
  
  public String getSelectedParticipantName() {
    return selectedParticipantName;
  }
  
  public void setSelectedParticipantName(String selectedParticipantName) {
    this.selectedParticipantName = selectedParticipantName;
  }
  
  public void setSelectedParticipantDisplayName(String selectedParticipantDisplayName) {
    this.selectedParticipantDisplayName = selectedParticipantDisplayName;
  }
  
  public String getSelectedParticipantDisplayName() {
    return selectedParticipantDisplayName;
  }

  public IllusionEventParticipantRole getSelectedParticipantRole() {
    return selectedParticipantRole;
  }
  
  public void setSelectedParticipantRole(IllusionEventParticipantRole selectedParticipantRole) {
    this.selectedParticipantRole = selectedParticipantRole;
  }
  
  public Long getSelectedParticipantUserId() {
    return selectedParticipantUserId;
  }
  
  public void setSelectedParticipantUserId(Long selectedParticipantUserId) {
    this.selectedParticipantUserId = selectedParticipantUserId;
  }
  
  public List<SelectItem> getRoleSelectItems() {
    return roleSelectItems;
  }
  
  public String saveSelectedParticipant() {
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(selectedParticipantId);
    
    illusionEventController.updateIllusionEventParticipantDisplayName(participant, selectedParticipantDisplayName);
    illusionEventController.updateIllusionEventParticipantRole(participant, selectedParticipantRole);
    
    return "/illusion/event-participants.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  private List<IllusionEventParticipant> organizers;
  private List<IllusionEventParticipant> participants;
  private List<IllusionEventParticipant> banned;
  private List<IllusionEventParticipant> approvalPending;
  private List<IllusionEventParticipant> waitingPayment;
  private List<IllusionEventParticipant> invited;
  private IllusionEventJoinMode eventJoinMode;
  private String joinUrl;
  private String eventUrl;
  private Long selectedParticipantId;
  private Long selectedParticipantUserId;
  private String selectedParticipantName;
  private String selectedParticipantDisplayName;
  private IllusionEventParticipantRole selectedParticipantRole;
  private List<SelectItem> roleSelectItems;
}
