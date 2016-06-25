package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.security.SecurityContext;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/dojoin", to = "/illusion/dojoin.jsf")
public class IllusionEventDoJoinBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    return String.format("/illusion/event-registration.jsf?faces-redirect=true&urlName=%s", getUrlName());
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
}
