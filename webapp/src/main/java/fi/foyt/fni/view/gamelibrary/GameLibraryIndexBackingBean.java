package fi.foyt.fni.view.gamelibrary;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.PublicationController;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/", to = "/gamelibrary/index.jsf")
public class GameLibraryIndexBackingBean extends AbstractGameLibraryListBackingBean {
  
	private static final int MAX_RECENT_PUBLICATIONS = 15;

  @Inject
  private PublicationController publicationController;

  @RequestAction
	public String load() {
	  return init(publicationController.listRecentPublications(MAX_RECENT_PUBLICATIONS));
	}
}