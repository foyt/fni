package fi.foyt.fni.view.gamelibrary;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.PublicationController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-publication-most-recent", 
		pattern = "/gamelibrary/", 
		viewId = "/gamelibrary/index.jsf"
  )
})
public class PublicationMostRecentListBackingBean extends AbstractPublicationListBackingBean {

	private static final int MAX_RECENT_PUBLICATIONS = 10;

	@Inject
	private PublicationController publicationController;

	@Inject
	private ForumController forumController;
	
	@URLAction
	public void init() {
		setPublications(publicationController.listRecentPublications(MAX_RECENT_PUBLICATIONS));
	}
	
}