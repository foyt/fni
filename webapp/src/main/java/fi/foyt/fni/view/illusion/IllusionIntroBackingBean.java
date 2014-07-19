package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.materials.IllusionGroupDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.security.UnauthorizedException;
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
    
    if (groupUser != null) {
      switch (groupUser.getRole()) {
        case GAMEMASTER:
        case PLAYER:
          return "/illusion/group.jsf?faces-redirect=true&urlName=" + illusionGroup.getUrlName();
        case BOT:
          throw new UnauthorizedException();
        default:
        break;
      }
    }
    
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
    
    joinMode = illusionGroup.getJoinMode();
    hasSignUpFee = illusionGroup.getSignUpFee() != null;
    if (hasSignUpFee) {
      Currency currency = illusionGroup.getSignUpFeeCurrency();
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
      currencyFormatter.setCurrency(currency);
      signUpFee = currencyFormatter.format(illusionGroup.getSignUpFee());
    }
    
    return null;
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  public void checkRole() {
    if (sessionController.isLoggedIn()) {
      IllusionGroup group = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
      IllusionGroupMember member = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, sessionController.getLoggedUser());
      if (member != null) {
        switch (member.getRole()) {
          case BANNED:
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.intro.bannedMessage"));
          break;
          case PENDING_APPROVAL:
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.intro.pendingApprovalMessage"));
          break;
          case WAITING_PAYMENT:
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.intro.waitingPaymentMessage"));
          break;
          default:
          break;
        }
      }
    }
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
  
  public IllusionGroupJoinMode getJoinMode() {
    return joinMode;
  }
  
  public boolean getHasSignUpFee() {
    return hasSignUpFee;
  }

  public String getSignUpFee() {
    return signUpFee;
  }
  
  private String text;
  private IllusionGroupJoinMode joinMode;
  private boolean hasSignUpFee;
  private String signUpFee;
}
