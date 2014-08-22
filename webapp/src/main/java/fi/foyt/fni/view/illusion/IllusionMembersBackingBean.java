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
@Join (path = "/illusion/group/{urlName}/members", to = "/illusion/members.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_GROUP_ACCESS, deferred = true)
@SecurityContext (context = "@urlName")
@SecurityParams ({
  @SecurityParam (name = "roles", value = "GAMEMASTER")
})
public class IllusionMembersBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private UserController userController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant member) {
    if ((member == null) || (member.getRole() != IllusionEventParticipantRole.GAMEMASTER)) {
      return "/error/access-denied.jsf";
    }

    gameMasters = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.GAMEMASTER);
    players = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.PLAYER);
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
    selectMember(approvalPending != null && approvalPending.size() > 0 ? approvalPending.get(0) : players.size() > 0 ? players.get(0) : gameMasters.get(0));
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
  
  public void selectMember(IllusionEventParticipant member) {
    selectedMemberId = member.getId();
    selectedMemberUserId = member.getUser().getId();
    selectedMemberName = member.getUser().getFullName();
    selectedMemberCharacterName = member.getCharacterName();
    selectedMemberRole = member.getRole();
  }

  public Long getSelectedMemberId() {
    return selectedMemberId;
  }
  
  public void setSelectedMemberId(Long selectedMemberId) {
    this.selectedMemberId = selectedMemberId;
  }
  
  public String getSelectedMemberName() {
    return selectedMemberName;
  }
  
  public void setSelectedMemberName(String selectedMemberName) {
    this.selectedMemberName = selectedMemberName;
  }
  
  public String getSelectedMemberCharacterName() {
    return selectedMemberCharacterName;
  }
  
  public void setSelectedMemberCharacterName(String selectedMemberCharacterName) {
    this.selectedMemberCharacterName = selectedMemberCharacterName;
  }

  public IllusionEventParticipantRole getSelectedMemberRole() {
    return selectedMemberRole;
  }
  
  public void setSelectedMemberRole(IllusionEventParticipantRole selectedMemberRole) {
    this.selectedMemberRole = selectedMemberRole;
  }
  
  public Long getSelectedMemberUserId() {
    return selectedMemberUserId;
  }
  
  public void setSelectedMemberUserId(Long selectedMemberUserId) {
    this.selectedMemberUserId = selectedMemberUserId;
  }
  
  public List<SelectItem> getRoleSelectItems() {
    return roleSelectItems;
  }
  
  public String saveSelectedMember() {
    IllusionEventParticipant member = illusionEventController.findIllusionEventParticipantById(selectedMemberId);
    
    illusionEventController.updateIllusionEventParticipantCharacterName(member, selectedMemberCharacterName);
    illusionEventController.updateIllusionEventParticipantRole(member, selectedMemberRole);
    
    return "/illusion/members.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  public String getMemberDisplayName(IllusionEventParticipant member) {
    User user = member.getUser();
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
  private Long selectedMemberId;
  private Long selectedMemberUserId;
  private String selectedMemberName;
  private String selectedMemberCharacterName;
  private IllusionEventParticipantRole selectedMemberRole;
  private List<SelectItem> roleSelectItems;
}
