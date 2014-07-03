package fi.foyt.fni.view.illusion;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}/materials", to = "/illusion/materials.jsf")
@LoggedIn
public class IllusionMaterialsBackingBean extends AbstractIllusionGroupBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;
  
  @Override
  public String init(IllusionGroup illusionGroup, IllusionGroupMember member) {
    if (member == null) {
      return "/error/access-denied.jsf";
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionGroupFolder folder = illusionGroup.getFolder();
    
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
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public List<Material> getMaterials() {
    return materials;
  }
  
  private List<Material> materials;
}
