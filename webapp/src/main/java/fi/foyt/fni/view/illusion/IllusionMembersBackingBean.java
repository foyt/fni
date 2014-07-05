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

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.security.LoggedIn;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/members", to = "/illusion/members.jsf")
@LoggedIn

// TODO: Security
public class IllusionMembersBackingBean extends AbstractIllusionGroupBackingBean {

  @Parameter
  private String urlName;
  
  @Inject
  private IllusionGroupController illusionGroupController;
  
  @Override
  public String init(IllusionGroup illusionGroup, IllusionGroupMember member) {
    if ((member == null) || (member.getRole() != IllusionGroupMemberRole.GAMEMASTER)) {
      return "/error/access-denied.jsf";
    }

    gameMasters = illusionGroupController.listIllusionGroupMembersByGroupAndRole(illusionGroup, IllusionGroupMemberRole.GAMEMASTER);
    players = illusionGroupController.listIllusionGroupMembersByGroupAndRole(illusionGroup, IllusionGroupMemberRole.PLAYER);
    banned = illusionGroupController.listIllusionGroupMembersByGroupAndRole(illusionGroup, IllusionGroupMemberRole.BANNED);
    groupJoinMode = illusionGroup.getJoinMode();
    if (groupJoinMode == IllusionGroupJoinMode.APPROVE) {
      approvalPending = illusionGroupController.listIllusionGroupMembersByGroupAndRole(illusionGroup, IllusionGroupMemberRole.PENDING_APPROVAL);
    }
    
    return null;
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  public void setDefaults() {
    selectMember(players.size() > 0 ? players.get(0) : gameMasters.get(0));
  }
  
  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public List<IllusionGroupMember> getGameMasters() {
    return gameMasters;
  }
  
  public List<IllusionGroupMember> getPlayers() {
    return players;
  }

  public List<IllusionGroupMember> getApprovalPending() {
    return approvalPending;
  }
  
  public List<IllusionGroupMember> getBanned() {
    return banned;
  }
  
  public IllusionGroupJoinMode getGroupJoinMode() {
    return groupJoinMode;
  }
  
  public void selectMember(IllusionGroupMember member) {
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

  public IllusionGroupMemberRole getSelectedMemberRole() {
    return selectedMemberRole;
  }
  
  public void setSelectedMemberRole(IllusionGroupMemberRole selectedMemberRole) {
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
    IllusionGroupMember member = illusionGroupController.findIllusionGroupMemberById(selectedMemberId);
    
    illusionGroupController.updateIllusionGroupMemberCharacterName(member, selectedMemberCharacterName);
    illusionGroupController.updateIllusionGroupMemberRole(member, selectedMemberRole);
    
    return "/illusion/members.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  private List<IllusionGroupMember> gameMasters;
  private List<IllusionGroupMember> players;
  private List<IllusionGroupMember> banned;
  private List<IllusionGroupMember> approvalPending;
  private IllusionGroupJoinMode groupJoinMode;
  private Long selectedMemberId;
  private Long selectedMemberUserId;
  private String selectedMemberName;
  private String selectedMemberCharacterName;
  private IllusionGroupMemberRole selectedMemberRole;
  private List<SelectItem> roleSelectItems;
}
