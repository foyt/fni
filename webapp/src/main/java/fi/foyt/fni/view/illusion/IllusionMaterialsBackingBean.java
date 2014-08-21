package fi.foyt.fni.view.illusion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.ComparatorUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/materials", to = "/illusion/materials.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_GROUP_ACCESS, deferred = true)
@SecurityContext (context = "@urlName")
public class IllusionMaterialsBackingBean extends AbstractIllusionGroupBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;
  
  @SuppressWarnings("unchecked")
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant member) {
    if (member == null) {
      return "/error/access-denied.jsf";
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionGroupFolder folder = illusionEvent.getFolder();
    
    materials = materialController.listMaterialsByFolderAndTypes(loggedUser, folder, Arrays.asList(
      MaterialType.DOCUMENT,
      MaterialType.IMAGE,
      MaterialType.PDF,
      MaterialType.FILE,
      MaterialType.BINARY,
      MaterialType.VECTOR_IMAGE,
      MaterialType.GOOGLE_DOCUMENT,
      MaterialType.DROPBOX_FILE   
    ));
    
    Collections.sort(materials, ComparatorUtils.chainedComparator(
      Arrays.asList(
        new MaterialTypeComparator(MaterialType.ILLUSION_FOLDER),
        new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER),
        new MaterialTypeComparator(MaterialType.FOLDER), 
        new TitleComparator())
      )
    );
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public List<Material> getMaterials() {
    return materials;
  }
  
  private List<Material> materials;
}
