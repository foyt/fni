package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.UserGroupMember;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/groups", to = "/illusion/event-groups.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext (context = "@urlName")
public class IllusionEventGroupsBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private Logger logger;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.GROUPS);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    groups = illusionEventController.listGroups(illusionEvent);
    List<IllusionEventParticipant> eventParticipants = illusionEventController.listIllusionEventParticipantsByEventAndRoles(illusionEvent, 
        IllusionEventParticipantRole.INVITED,
        IllusionEventParticipantRole.WAITING_PAYMENT,
        IllusionEventParticipantRole.PENDING_APPROVAL,
        IllusionEventParticipantRole.PARTICIPANT);
    
    participants = new ArrayList<>(eventParticipants.size());
    for (IllusionEventParticipant eventParticipant : eventParticipants) {
      String roleDetails = null;
      switch (eventParticipant.getRole()) {
        case INVITED:
          roleDetails = ExternalLocales.getText(sessionController.getLocale(), "illusion.eventGroups.roleDetailsInvited");
        break;
        case WAITING_PAYMENT:
          roleDetails = ExternalLocales.getText(sessionController.getLocale(), "illusion.eventGroups.roleDetailsWaitingPaymement");
        break;
        case PENDING_APPROVAL:
          roleDetails = ExternalLocales.getText(sessionController.getLocale(), "illusion.eventGroups.roleDetailsPendingApproval");
        break;
        default:
        break;
      }
      
      String displayName = getParticipantDisplayName(eventParticipant);
      participants.add(new Participant(eventParticipant.getId(), roleDetails != null ? String.format("%s (%s)", displayName, roleDetails) : displayName));
    }
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public List<IllusionEventGroup> getGroups() {
    return groups;
  }
  
  public List<Participant> getParticipants() {
    return participants;
  }
  
  public String getSelectedGroupName() {
    return selectedGroupName;
  }
  
  public void setSelectedGroupName(String selectedGroupName) {
    this.selectedGroupName = selectedGroupName;
  }
  
  public List<UserGroupMember> getMembers() {
    return members;
  }
  
  public Long getSelectedGroupId() {
    return selectedGroupId;
  }
  
  public void setSelectedGroupId(Long selectedGroupId) {
    this.selectedGroupId = selectedGroupId;
  }
  
  public List<Long> getSelectedGroupMemberParticipantIds() {
    return selectedGroupMemberParticipantIds;
  }
  
  public void setSelectedGroupMemberParticipantIds(List<Long> selectedGroupMemberParticipantIds) {
    this.selectedGroupMemberParticipantIds = selectedGroupMemberParticipantIds;
  }
  
  public void selectGroup(IllusionEventGroup group) {
    selectedGroupId = group.getId();
    selectedGroupName = group.getName();
    members = illusionEventController.listGroupMembers(group);
    selectedGroupMemberParticipantIds = new ArrayList<>();
    
    for (UserGroupMember member : members) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(group.getEvent(), member.getUser());
      if (participant != null) {
        selectedGroupMemberParticipantIds.add(participant.getId());
      }
    }
  }
  
  public String saveGroup() {
    IllusionEventGroup group = illusionEventController.findGroupById(selectedGroupId);
    List<UserGroupMember> currentMembers = illusionEventController.listGroupMembers(group);
    Set<Long> addParticipantIds = new HashSet<>();
    Set<Long> removeParticipantIds = new HashSet<>();
    
    for (UserGroupMember currentMember : currentMembers) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(group.getEvent(), currentMember.getUser());
      if (participant != null) {
        removeParticipantIds.add(participant.getId());
      } else {
        logger.warning(String.format("User %d of group %d is not a member of an event %d", currentMember.getUser().getId(), group.getId(), group.getEvent().getId()));
      }
    }
    
    for (Long selectedGroupMemberParticipantId : selectedGroupMemberParticipantIds) {
      if (!removeParticipantIds.contains(selectedGroupMemberParticipantId)) {
        addParticipantIds.add(selectedGroupMemberParticipantId);
      }
      
      removeParticipantIds.remove(selectedGroupMemberParticipantId);
    }
    
    for (Long participantId : removeParticipantIds) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
      UserGroupMember groupMember = illusionEventController.findGroupMemberByGroupAndUser(group, participant.getUser());
      if (groupMember != null) {
        illusionEventController.deleteGroupMember(groupMember);
      }
    }
    
    for (Long participantId : addParticipantIds) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
      illusionEventController.createGroupMember(group, participant.getUser());
    }
    
    illusionEventController.updateGroupName(group, getSelectedGroupName());
    groups = illusionEventController.listGroups(group.getEvent());
    
    return "/illusion/event-groups.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  private List<IllusionEventGroup> groups;
  private List<Participant> participants;
  private Long selectedGroupId;
  private List<UserGroupMember> members;
  private String selectedGroupName;
  private List<Long> selectedGroupMemberParticipantIds;
  
  public class Participant {
    
    public Participant(Long id, String displayName) {
      this.id = id;
      this.displayName = displayName;
    }
    
    public Long getId() {
      return id;
    }
    
    public String getDisplayName() {
      return displayName;
    }
    
    private Long id;
    private String displayName;
  }
}
