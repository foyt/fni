package fi.foyt.fni.view.forge;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.materials.MaterialController;

@Join(path = "/forge/public", to = "/forge/public.jsf")
@RequestScoped
@Named
@Stateful
public class ForgePublicBackingBean extends AbstractForgePublicViewBackingBean {
  
  private static final int LATEST_COUNT = 5;
  private static final int POPULAR_COUNT = 5;

  @Inject
  private MaterialController materialController;
  
  @RequestAction
  public String init() {
    this.tags = toTagBeans(materialController.listPublicMaterialTagsWithCounts(TAG_COUNT));
    this.randomMaterial = toMaterialBean(materialController.findRandomPublicMaterial());
    this.latestMaterials = toMaterialBeans(materialController.listLatestPublicMaterials(LATEST_COUNT));
    this.mostPopularMaterial = toMaterialBeans(materialController.listMostPopuralMaterials(POPULAR_COUNT));
    
    return null;
  }
  
  public PublicMaterialBean getRandomMaterial() {
    return randomMaterial;
  }
  
  public List<PublicMaterialBean> getLatestMaterials() {
    return latestMaterials;
  }
  
  public List<PublicMaterialBean> getMostPopularMaterial() {
    return mostPopularMaterial;
  }
  
  public List<PublicTagBean> getTags() {
    return tags;
  }

  private PublicMaterialBean randomMaterial;
  private List<PublicMaterialBean> latestMaterials;
  private List<PublicMaterialBean> mostPopularMaterial;
  private List<PublicTagBean> tags;
}
