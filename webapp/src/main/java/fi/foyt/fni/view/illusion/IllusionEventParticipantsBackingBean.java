package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.security.SecurityParam;
import fi.foyt.fni.security.SecurityParams;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/participants", to = "/illusion/event-participants.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_ACCESS, deferred = true)
@SecurityContext (context = "@urlName")
@SecurityParams ({
  @SecurityParam (name = "roles", value = "GAMEMASTER")
})
public class IllusionEventParticipantsBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private UserController userController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      return "/error/access-denied.jsf";
    }

    gameMasters = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.ORGANIZER);
    players = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.PARTICIPANT);
    banned = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.BANNED);
    eventJoinMode = illusionEvent.getJoinMode();
    if (eventJoinMode == IllusionEventJoinMode.APPROVE) {
      approvalPending = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.PENDING_APPROVAL);
    }
    
    waitingPayment = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.WAITING_PAYMENT);
    invited = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.INVITED);
    
    String eventUrl = systemSettingsController.getSiteUrl(false, true);
    if (StringUtils.isNotBlank(eventUrl)) {
      eventUrl += "/illusion/event/" + illusionEvent.getUrlName();
    }
    
    joinUrl = eventUrl + "/dojoin?ref=inv";
    introUrl = eventUrl + "/intro?ref=inv";
    
    return null;
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  public void setDefaults() {
    selectParticipant(approvalPending != null && approvalPending.size() > 0 ? approvalPending.get(0) : players.size() > 0 ? players.get(0) : gameMasters.get(0));
  }
  
  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public List<IllusionEventParticipant> getGameMasters() {
    return gameMasters;
  }
  
  public List<IllusionEventParticipant> getPlayers() {
    return players;
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
  
  public String getIntroUrl() {
    return introUrl;
  }
  
  public void selectParticipant(IllusionEventParticipant participant) {
    selectedParticipantId = participant.getId();
    selectedParticipantUserId = participant.getUser().getId();
    selectedParticipantName = participant.getUser().getFullName();
    selectedParticipantCharacterName = participant.getCharacterName();
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
  
  public String getSelectedParticipantCharacterName() {
    return selectedParticipantCharacterName;
  }
  
  public void setSelectedParticipantCharacterName(String selectedParticipantCharacterName) {
    this.selectedParticipantCharacterName = selectedParticipantCharacterName;
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
    
    illusionEventController.updateIllusionEventParticipantCharacterName(participant, selectedParticipantCharacterName);
    illusionEventController.updateIllusionEventParticipantRole(participant, selectedParticipantRole);
    
    return "/illusion/event-participants.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  public String getParticipantDisplayName(IllusionEventParticipant participant) {
    User user = participant.getUser();
    String result = user.getFullName();
    if (StringUtils.isNotBlank(result)) {
      return result;
    }
    
    return "<" + userController.getUserPrimaryEmail(user) + ">";
  }
  
  private List<IllusionEventParticipant> gameMasters;
  private List<IllusionEventParticipant> players;
  private List<IllusionEventParticipant> banned;
  private List<IllusionEventParticipant> approvalPending;
  private List<IllusionEventParticipant> waitingPayment;
  private List<IllusionEventParticipant> invited;
  private IllusionEventJoinMode eventJoinMode;
  private String joinUrl;
  private String introUrl;
  private Long selectedParticipantId;
  private Long selectedParticipantUserId;
  private String selectedParticipantName;
  private String selectedParticipantCharacterName;
  private IllusionEventParticipantRole selectedParticipantRole;
  private List<SelectItem> roleSelectItems;
}
