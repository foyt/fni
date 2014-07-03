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

import fi.foyt.fni.materials.IllusionGroupDocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.utils.data.FileData;

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
  
  private String text;
}
