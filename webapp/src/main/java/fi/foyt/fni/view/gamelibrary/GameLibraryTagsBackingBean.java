package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/tags/{tag}", to = "/gamelibrary/taglist.jsf")
public class GameLibraryTagsBackingBean {
  
  @Parameter
  @Matches ("[a-zA-Z0-9_\\/.-\\:,\\ ]{1,}")
  private String tag;
  
	@Inject
	private PublicationController publicationController;
	
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