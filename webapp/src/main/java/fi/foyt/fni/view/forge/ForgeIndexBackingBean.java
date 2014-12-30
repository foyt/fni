package fi.foyt.fni.view.forge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.ComparatorUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@Join (path = "/forge/", to = "/forge/index.jsf")
@RequestScoped
@Named
@Stateful
@LoggedIn
public class ForgeIndexBackingBean extends AbstractForgeMaterialViewBackingBean {

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;
  
  @SuppressWarnings("unchecked")
  @RequestAction
  public String load() {
    List<Material> materials = materialController.listMaterialsByFolder(sessionController.getLoggedUser(), null);

    Collections.sort(materials, ComparatorUtils.chainedComparator(
      Arrays.asList(
        new MaterialTypeComparator(MaterialType.ILLUSION_FOLDER),
        new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER),
        new MaterialTypeComparator(MaterialType.FOLDER), 
        new TitleComparator())
      )
    );
    
    setMaterials(materials);
    
    return null;
  }

}
