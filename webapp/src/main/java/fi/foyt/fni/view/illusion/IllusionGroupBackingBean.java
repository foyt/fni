package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.materials.IllusionGroupDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.FileData;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}", to = "/illusion/group.jsf")
@LoggedIn
public class IllusionGroupBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private IllusionGroupDocumentController illusionGroupDocumentController;

  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  public String init() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    if (illusionGroup == null) {
      return "/error/not-found.jsf";
    }
    
    IllusionGroupFolder folder = illusionGroup.getFolder();
    IllusionGroupDocument indexDocument = illusionGroupDocumentController.findByFolderAndDocumentType(folder, IllusionGroupDocumentType.INDEX);
    if (indexDocument != null) {
      try {
        FileData indexData = materialController.getMaterialData(null, sessionController.getLoggedUser(), indexDocument);
        if (indexData != null) {
          indexText = new String(indexData.getData(), "UTF-8");
        }
      } catch (IOException | GeneralSecurityException e) {
        logger.log(Level.WARNING, "Could not retreive group index text", e);
      }
    }
    
    id = illusionGroup.getId();
    name = illusionGroup.getName();
    description = illusionGroup.getDescription();
  
    return null;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
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
  
  public String getIndexText() {
    return indexText;
  }
  
  private Long id;
  private String name;
  private String description;
  private String indexText;
}
