package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/tags/{tag}", to = "/gamelibrary/taglist.jsf")
public class GameLibraryTagsBackingBean {
  
  @Parameter
  private String tag;
  
	@Inject
	private PublicationController publicationController;

  @Inject
	private GameLibraryTagController gameLibraryTagController;

  @RequestAction
  public String load() {
    if (StringUtils.isBlank(tag)) {
      return "/error/not-found.jsf"; 
    }
    
    GameLibraryTag libraryTag = gameLibraryTagController.findTagByText(tag);
    if (libraryTag == null) {
      return "/error/not-found.jsf"; 
    }
    
    return null;
  }
	
	public List<Publication> getPublications() {
		return publicationController.listPublicationsByPublishedAndTags(false, tag);
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
}