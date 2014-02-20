package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { @URLMapping(id = "illusion-index", pattern = "/illusion/", viewId = "/illusion/index.jsf") })
public class IllusionIndexBackingBean {

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionGroupController illusionGroupController;

  @URLAction
  @LoggedIn
  public void init() {
    User loggedUser = sessionController.getLoggedUser();
    
    gameMasterGroups = illusionGroupController.listIllusionGroupsByUserAndRole(loggedUser, IllusionGroupUserRole.GAMEMASTER);
    playerGroups = illusionGroupController.listIllusionGroupsByUserAndRole(loggedUser, IllusionGroupUserRole.PLAYER);
  }
  
  public List<IllusionGroup> getGameMasterGroups() {
    return gameMasterGroups;
  }
  
  public List<IllusionGroup> getPlayerGroups() {
    return playerGroups;
  }
  
  public Long getGroupPlayerCount(IllusionGroup group) {
    return illusionGroupController.countIllusionGroupUsersByGroupAndRole(group, IllusionGroupUserRole.PLAYER);
  }
  
  private List<IllusionGroup> gameMasterGroups;
  private List<IllusionGroup> playerGroups;
}
