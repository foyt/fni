package fi.foyt.fni.view.illusion;

import javax.inject.Inject;

import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

public abstract class AbstractIllusionGroupBackingBean {

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  public String basicInit() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    if (illusionGroup == null) {
      return "/error/not-found.jsf";
    }
    
    User loggedUser = sessionController.getLoggedUser();

    IllusionGroupMember groupUser = illusionGroupController.findIllusionGroupMemberByUserAndGroup(illusionGroup, loggedUser);
    if (groupUser == null) {
      return "/error/access-denied.jsf";
    }

    IllusionGroupFolder folder = illusionGroup.getFolder();
    
    id = illusionGroup.getId();
    name = illusionGroup.getName();
    description = illusionGroup.getDescription();
    illusionFolderPath = folder.getPath();
    mayEditMaterials = groupUser.getRole() == IllusionGroupMemberRole.GAMEMASTER;
  
    return init(illusionGroup, groupUser);
  }

  public abstract String init(IllusionGroup illusionGroup, IllusionGroupMember groupUser);
  public abstract String getUrlName();
  
  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getIllusionFolderPath() {
    return illusionFolderPath;
  }
  
  public boolean getMayEditMaterials() {
    return mayEditMaterials;
  }
  
  private Long id;
  private String name;
  private String description;
  private String illusionFolderPath;
  private boolean mayEditMaterials;
}
