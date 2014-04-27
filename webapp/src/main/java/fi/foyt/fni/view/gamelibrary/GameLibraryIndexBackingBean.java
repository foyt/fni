package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/", to = "/gamelibrary/index.jsf")
public class GameLibraryIndexBackingBean {
  
	private static final int MAX_RECENT_PUBLICATIONS = 15;

	@Inject
	private PublicationController publicationController;
	
	public List<Publication> getPublications() {
		return publicationController.listRecentPublications(MAX_RECENT_PUBLICATIONS);
	}
	
}