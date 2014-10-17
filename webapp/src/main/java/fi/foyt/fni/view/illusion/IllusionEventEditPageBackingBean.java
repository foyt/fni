package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.view.illusion.IllusionEventNavigationController.SelectedItem;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/edit-page", to = "/illusion/event-edit-page.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext(context = "@urlName")
public class IllusionEventEditPageBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Parameter
  private Long id;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedItem(SelectedItem.MANAGE_PAGES);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    IllusionEventDocument page = (IllusionEventDocument) materialController.findMaterialById(getId());
    if (page.getDocumentType() != IllusionEventDocumentType.PAGE) {
      return "/error/not-found.jsf";
    }
    
    pageTitle = page.getTitle();
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  @Override
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getPageTitle() {
    return pageTitle;
  }
  
  private String pageTitle;
}
