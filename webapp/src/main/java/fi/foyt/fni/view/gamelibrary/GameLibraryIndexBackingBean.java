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
		id = "gamelibrary-index", 
		pattern = "/gamelibrary/", 
		viewId = "/gamelibrary/index.jsf"
  )
})
public class GameLibraryIndexBackingBean {
  
	private static final int MAX_RECENT_PUBLICATIONS = 5;

	@Inject
	private PublicationController publicationController;
	
	public List<Publication> getPublications() {
		return publicationController.listRecentPublications(MAX_RECENT_PUBLICATIONS);
	}
	
}