package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
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
  public String init(IllusionGroup illusionGroup, IllusionGroupMember groupUser) {
    if (groupUser.getRole() != IllusionGroupMemberRole.GAMEMASTER) {
      return "/error/access-denied.jsf";
    }

    gameMasters = illusionGroupController.listIllusionGroupMembersByGroupAndRole(illusionGroup, IllusionGroupMemberRole.GAMEMASTER);
    players = illusionGroupController.listIllusionGroupMembersByGroupAndRole(illusionGroup, IllusionGroupMemberRole.PLAYER);
    
    return null;
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  public void postConstruct() {
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
  
  public void selectMember(IllusionGroupMember member) {
    selectedMemberId = member.getId();
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
  
  public String saveSelectedMember() {
    IllusionGroupMember member = illusionGroupController.findIllusionGroupMemberById(selectedMemberId);
    
    illusionGroupController.updateIllusionGroupMemberCharacterName(member, selectedMemberCharacterName);
    illusionGroupController.updateIllusionGroupMemberRole(member, selectedMemberRole);
    
    return "/illusion/members.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  private List<IllusionGroupMember> gameMasters;
  private List<IllusionGroupMember> players;
  
  private Long selectedMemberId;
  private String selectedMemberName;
  private String selectedMemberCharacterName;
  private IllusionGroupMemberRole selectedMemberRole;
}
