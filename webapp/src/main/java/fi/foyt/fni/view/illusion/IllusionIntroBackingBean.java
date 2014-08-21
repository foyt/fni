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

import fi.foyt.fni.materials.IllusionGroupDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/intro", to = "/illusion/intro.jsf")
public class IllusionIntroBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;
  
  @Parameter
  private String ref;

  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupDocumentController illusionGroupDocumentController;

  @Inject
  private MaterialController materialController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    IllusionGroupFolder folder = illusionEvent.getFolder();
    
    if (participant != null) {
      memberRole = participant.getRole();
      switch (memberRole) {
        case GAMEMASTER:
        case PLAYER:
          return "/illusion/group.jsf?faces-redirect=true&urlName=" + illusionEvent.getUrlName();
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
    
    joinMode = illusionEvent.getJoinMode();
    hasSignUpFee = illusionEvent.getSignUpFee() != null;
    if (hasSignUpFee) {
      Currency currency = illusionEvent.getSignUpFeeCurrency();
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
      currencyFormatter.setCurrency(currency);
      signUpFee = currencyFormatter.format(illusionEvent.getSignUpFee());
    }
    
    return null;
  }
  
  @RequestAction
  @Deferred
  @IgnorePostback
  public void checkRole() {
    if (memberRole != null) {
      switch (memberRole) {
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
  
  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public String getRef() {
    return ref;
  }
  
  public void setRef(String ref) {
    this.ref = ref;
  }
  
  public String getText() {
    return text;
  }
  
  public IllusionEventJoinMode getJoinMode() {
    return joinMode;
  }
  
  public IllusionEventParticipantRole getMemberRole() {
    return memberRole;
  }
  
  public boolean getHasSignUpFee() {
    return hasSignUpFee;
  }

  public String getSignUpFee() {
    return signUpFee;
  }
  
  private String text;
  private IllusionEventJoinMode joinMode;
  private IllusionEventParticipantRole memberRole;
  private boolean hasSignUpFee;
  private String signUpFee;
}
