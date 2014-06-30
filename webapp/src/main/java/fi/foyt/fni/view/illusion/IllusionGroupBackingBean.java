package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/group/{urlName}", to = "/illusion/group.jsf")
public class IllusionGroupBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionGroupController illusionGroupController;
  
  @RequestAction
  public String init() {
    IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(getUrlName());
    if (illusionGroup == null) {
      return "/error/not-found.jsf";
    }
    
    id = illusionGroup.getId();
    name = illusionGroup.getName();
    description = illusionGroup.getDescription();
  
    if (illusionGroup.getIndexDocument() != null) {
      welcomeContent = illusionGroup.getIndexDocument().getData();
    } 
    
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
  
  public String getWelcomeContent() {
    return welcomeContent;
  }
  
  private Long id;
  private String name;
  private String description;
  private String welcomeContent;
}
