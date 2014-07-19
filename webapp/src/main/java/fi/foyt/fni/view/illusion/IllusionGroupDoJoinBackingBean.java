package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/dojoin", to = "/illusion/dojoin.jsf")
public class IllusionGroupDoJoinBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  @Deferred
  @LoggedIn
  public String init() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    if (illusionGroup == null) {
      return "/error/not-found.jsf";
    }
    
    User loggedUser = sessionController.getLoggedUser();
    IllusionGroupMember groupMember = illusionGroupController.findIllusionGroupMemberByUserAndGroup(illusionGroup, loggedUser);
    if (groupMember == null) {
      switch (illusionGroup.getJoinMode()) {
        case APPROVE:
          illusionGroupController.createIllusionGroupMember(loggedUser, illusionGroup, null, IllusionGroupMemberRole.PENDING_APPROVAL);
          FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("illusion.intro.approvalPendingMessage"));
          return "/illusion/intro.jsf?faces-redirect=true&urlName=" + getUrlName();
        case OPEN:
          illusionGroupController.createIllusionGroupMember(loggedUser, illusionGroup, null, IllusionGroupMemberRole.PLAYER);
          return "/illusion/group.jsf?faces-redirect=true&urlName=" + getUrlName();
        default:
          return "/error/access-denied.jsf";
      }      
    } else {
      switch (groupMember.getRole()) {
        case BANNED:
        case BOT:
          return "/error/access-denied.jsf";
        case PENDING_APPROVAL:
        case WAITING_PAYMENT:
          return "/illusion/intro.jsf?faces-redirect=true&urlName=" + getUrlName();
        case GAMEMASTER:
        case PLAYER:
          return "/illusion/group.jsf?faces-redirect=true&urlName=" + getUrlName();
      }
    }
    
    return null;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
}
