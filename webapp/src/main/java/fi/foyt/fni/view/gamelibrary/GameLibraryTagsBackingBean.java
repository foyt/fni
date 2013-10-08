package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-tagslist", 
		pattern = "/gamelibrary/tags/#{gameLibraryTagsBackingBean.tag}", 
		viewId = "/gamelibrary/taglist.jsf"
  )
})
public class GameLibraryTagsBackingBean {
  
	@Inject
	private PublicationController publicationController;
	
	public List<Publication> getPublications() {
		return publicationController.listPublicationsByTags(tag);
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	private String tag;
}