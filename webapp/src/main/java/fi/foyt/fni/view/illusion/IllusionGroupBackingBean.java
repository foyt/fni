package fi.foyt.fni.view.illusion;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.security.LoggedIn;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
    id = "illusion-space", 
    pattern = "/illusion/group/#{illusionGroupBackingBean.urlName}", 
    viewId = "/illusion/group.jsf"
  ) 
})
public class IllusionGroupBackingBean {
  
  @Inject
  private IllusionGroupController illusionGroupController;

  @URLAction
  @LoggedIn
  public void load() throws FileNotFoundException {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionSpaceByUrlName(getUrlName());
    if (illusionGroup == null) {
      throw new FileNotFoundException();
    }
    
    id = illusionGroup.getId();
    name = illusionGroup.getName();
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
  
  public String getName() {
    return name;
  }

  private Long id;
  private String urlName;
  private String name;
}
