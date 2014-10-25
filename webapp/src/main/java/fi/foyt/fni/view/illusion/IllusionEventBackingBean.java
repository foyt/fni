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

import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.materials.IllusionEventDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}", to = "/illusion/event.jsf")
public class IllusionEventBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;
  
  @Parameter
  private String ref;

  @Parameter
  private String ignoreMessages;

  @Inject
  private Logger logger;

  @Inject
  private IllusionEventDocumentController illusionEventDocumentController;

  @Inject
  private MaterialController materialController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant member) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.INDEX);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    if (member != null) {
      memberRole = member.getRole();
      switch (memberRole) {
        case BOT:
          return "/error/access-denied.jsf";
        case BANNED:
        case PENDING_APPROVAL:
        case WAITING_PAYMENT:
        case INVITED:
        case ORGANIZER:
        case PARTICIPANT:
        break;
      }
    }

    IllusionEventFolder folder = illusionEvent.getFolder();
    
    IllusionEventDocument indexDocument = illusionEventDocumentController.findByFolderAndDocumentType(folder, IllusionEventDocumentType.INDEX);
    if (indexDocument != null) {
      try {
        FileData indexData = materialController.getMaterialData(null, null, indexDocument);
        if (indexData != null) {
          indexText = new String(indexData.getData(), "UTF-8");
        }
      } catch (IOException | GeneralSecurityException e) {
        logger.log(Level.WARNING, "Could not retreive event index text", e);
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
  public void showWarnings() {
    if (!"true".equals(getIgnoreMessages())) {
      if (memberRole != null) {
        switch (memberRole) {
          case BANNED:
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.event.bannedMessage"));
          break;
          case PENDING_APPROVAL:
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.event.pendingApprovalMessage"));
          break;
          case WAITING_PAYMENT:
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.event.waitingPaymentMessage"));
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

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public String getIndexText() {
    return indexText;
  }

  public IllusionEventJoinMode getJoinMode() {
    return joinMode;
  }
  
  public boolean getHasSignUpFee() {
    return hasSignUpFee;
  }

  public String getSignUpFee() {
    return signUpFee;
  }
  
  public String getRef() {
    return ref;
  }
  
  public void setRef(String ref) {
    this.ref = ref;
  }
  
  public String getIgnoreMessages() {
    return ignoreMessages;
  }
  
  public void setIgnoreMessages(String ignoreMessages) {
    this.ignoreMessages = ignoreMessages;
  }
  
  public IllusionEventParticipantRole getMemberRole() {
    return memberRole;
  }
  
  private String indexText;
  private IllusionEventJoinMode joinMode;
  private boolean hasSignUpFee;
  private String signUpFee;
  private IllusionEventParticipantRole memberRole;
}
