package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.materials.IllusionEventDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;

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
  private String pageId;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private IllusionEventDocumentController illusionEventDocumentController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MANAGE_PAGES);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    IllusionEventDocument page = null;
    
    if (StringUtils.isNumeric(getPageId())) {
      Long documentId = NumberUtils.createLong(getPageId());
      
      Material material = materialController.findMaterialById(documentId);
      if (!(material instanceof IllusionEventDocument)) {
        return "/error/not-found.jsf";
      }
      
      page = (IllusionEventDocument) material;
      if (page.getDocumentType() != IllusionEventDocumentType.PAGE) {
        return "/error/not-found.jsf";
      }      
    } else {
      if ("INDEX".equals(getPageId())) {
        page = illusionEventDocumentController.findByFolderAndDocumentType(illusionEvent.getFolder(), IllusionEventDocumentType.INDEX);
      } else {
        return "/error/not-found.jsf";
      }
    }

    if (page == null) {
      return "/error/not-found.jsf";
    }
    
    pageTitle = page.getTitle();
    documentId = page.getId();
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public String getPageId() {
    return pageId;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }
  
  public String getPageTitle() {
    return pageTitle;
  }
  
  public Long getDocumentId() {
    return documentId;
  }
  
  private String pageTitle;
  private Long documentId;
}
