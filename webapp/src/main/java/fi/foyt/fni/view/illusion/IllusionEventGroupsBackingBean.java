package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.security.SecurityParam;
import fi.foyt.fni.security.SecurityParams;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/groups", to = "/illusion/event-groups.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_ACCESS, deferred = true)
@SecurityContext (context = "@urlName")
@SecurityParams ({
  @SecurityParam (name = "roles", value = "ORGANIZER")
})
public class IllusionEventGroupsBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventGroupController illusionEventGroupController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    groups = illusionEventGroupController.listGroups(illusionEvent);
    participants = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.PARTICIPANT);

    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public List<IllusionEventGroup> getGroups() {
    return groups;
  }
  
  public List<IllusionEventParticipant> getParticipants() {
    return participants;
  }
  
  public String getSelectedGroupName() {
    return selectedGroupName;
  }
  
  public void setSelectedGroupName(String selectedGroupName) {
    this.selectedGroupName = selectedGroupName;
  }
  
  public List<IllusionEventGroupMember> getMembers() {
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
    members = illusionEventGroupController.listMembers(group);
    selectedGroupMemberParticipantIds = new ArrayList<>();
    
    for (IllusionEventGroupMember member : members) {
      selectedGroupMemberParticipantIds.add(member.getParticipant().getId());
    }
  }
  
  public void saveGroup() {
    IllusionEventGroup group = illusionEventGroupController.findGroupById(selectedGroupId);
    List<IllusionEventGroupMember> currentMembers = illusionEventGroupController.listMembers(group);
    Set<Long> addParticipantIds = new HashSet<>();
    Set<Long> removeParticipantIds = new HashSet<>();
    
    for (IllusionEventGroupMember currentMember : currentMembers) {
      removeParticipantIds.add(currentMember.getParticipant().getId());
    }
    
    for (Long selectedGroupMemberParticipantId : selectedGroupMemberParticipantIds) {
      if (!removeParticipantIds.contains(selectedGroupMemberParticipantId)) {
        addParticipantIds.add(selectedGroupMemberParticipantId);
      }
      
      removeParticipantIds.remove(selectedGroupMemberParticipantId);
    }
    
    for (Long participantId : removeParticipantIds) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
      IllusionEventGroupMember member = illusionEventGroupController.findMemberByGroupAndParticipant(group, participant);
      if (member != null) {
        illusionEventGroupController.deleteMember(member);
      }
    }
    
    for (Long participantId : addParticipantIds) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
      illusionEventGroupController.createMember(group, participant);
    }
    
    illusionEventGroupController.updateGroupName(group, getSelectedGroupName());
    groups = illusionEventGroupController.listGroups(group.getEvent());
  }
private List<IllusionEventGroup> groups;
  private List<IllusionEventParticipant> participants;
  private Long selectedGroupId;
  private List<IllusionEventGroupMember> members;
  private String selectedGroupName;
  private List<Long> selectedGroupMemberParticipantIds;
}
