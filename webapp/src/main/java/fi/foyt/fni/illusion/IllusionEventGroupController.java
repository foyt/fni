package fi.foyt.fni.illusion;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.illusion.IllusionEventGroupDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventGroupMemberDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroupMember;

@Dependent
@Stateless
public class IllusionEventGroupController {
  
  @Inject
  private IllusionEventGroupDAO illusionEventParticipantGroupDAO;

  @Inject
  private IllusionEventGroupMemberDAO illusionEventParticipantGroupMemberDAO;

  /* Group */

  public IllusionEventGroup findGroupById(Long id) {
    return illusionEventParticipantGroupDAO.findById(id);
  }
  
  public IllusionEventGroup createGroup(IllusionEvent event, String name) {
    return illusionEventParticipantGroupDAO.create(event, name);
  }
  
  public List<IllusionEventGroup> listGroups(IllusionEvent event) {
    return illusionEventParticipantGroupDAO.listByEvent(event);
  }
  
  public IllusionEventGroup updateGroupName(IllusionEventGroup group, String name) {
    return illusionEventParticipantGroupDAO.updateName(group, name);
  }
  
  public void deleteGroup(IllusionEventGroup group) {
    illusionEventParticipantGroupDAO.delete(group);
  }
  
  /* Member */
  
  public IllusionEventGroupMember createMember(IllusionEventGroup group, IllusionEventParticipant participant) {
    return illusionEventParticipantGroupMemberDAO.create(group, participant);
  }
  
  public List<IllusionEventGroupMember> listMembers(IllusionEventGroup group) {
    return illusionEventParticipantGroupMemberDAO.listByGroup(group); 
  }
  
  public void deleteMember(IllusionEventGroupMember member) {
    illusionEventParticipantGroupMemberDAO.delete(member);
  }

  public IllusionEventGroupMember findMemberByGroupAndParticipant(IllusionEventGroup group, IllusionEventParticipant participant) {
    return illusionEventParticipantGroupMemberDAO.findByGroupAndParticipant(group, participant);
  }
  
}
