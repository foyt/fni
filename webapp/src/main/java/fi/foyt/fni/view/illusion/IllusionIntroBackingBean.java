package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.materials.IllusionGroupDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/intro", to = "/illusion/intro.jsf")
public class IllusionIntroBackingBean extends AbstractIllusionGroupBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupDocumentController illusionGroupDocumentController;

  @Inject
  private MaterialController materialController;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private SessionController sessionController;
  
  @Override
  public String init(IllusionGroup illusionGroup, IllusionGroupMember groupUser) {
    IllusionGroupFolder folder = illusionGroup.getFolder();
    
    IllusionGroupDocument introDocument = illusionGroupDocumentController.findByFolderAndDocumentType(folder, IllusionGroupDocumentType.INTRO);
    if (introDocument != null) {
      try {
        FileData introData = materialController.getMaterialData(null, null, introDocument);
        if (introData != null) {
          text = new String(introData.getData(), "UTF-8");
        }
      } catch (IOException | GeneralSecurityException e) {
        logger.log(Level.WARNING, "Could not retreive group index text", e);
      }
    }
    
    canBeJoined = illusionGroup.getJoinMode() == IllusionGroupJoinMode.OPEN || illusionGroup.getJoinMode() == IllusionGroupJoinMode.APPROVE;
    
    return null;
  }
  
  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public String getText() {
    return text;
  }
  
  public boolean getCanBeJoined() {
    return canBeJoined;
  }
  
  @LoggedIn
  public String join() {
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
          FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("illusion.intro.approvalPendingMessage"));
        break;
        case OPEN:
          illusionGroupController.createIllusionGroupMember(loggedUser, illusionGroup, null, IllusionGroupMemberRole.PLAYER);
          return "/illusion/group.jsf?faces-redirect=true&urlName=" + getUrlName();
        default:
          return "/error/access-denied.jsf";
      }      
    }
    
    
    return null;
  }
  
  private String text;
  private boolean canBeJoined;
}
