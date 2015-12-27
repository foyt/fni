package fi.foyt.fni.view.gamelibrary;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/", to = "/gamelibrary/index.jsf")
public class GameLibraryIndexBackingBean extends AbstractGameLibraryListBackingBean {
  
	private static final int MAX_RECENT_PUBLICATIONS = 15;

  @Inject
  private PublicationController publicationController;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private SystemSettingsController systemSettingsController;

  @RequestAction
	public String load() {
    String locale = sessionController.getLocale().toString();
    
    atomFeedUrl = String.format("%s/gamelibrary/feed/?type=atom_0.3&locale=%s", systemSettingsController.getSiteUrl(true, true), locale);
    rssFeedUrl = String.format("%s/gamelibrary/feed/?type=rss_2.0&locale=%s", systemSettingsController.getSiteUrl(true, true), locale);
    
	  return init(publicationController.listRecentBookPublications(MAX_RECENT_PUBLICATIONS));
	}
  
  public String getAtomFeedUrl() {
    return atomFeedUrl;
  }
  
  public String getRssFeedUrl() {
    return rssFeedUrl;
  }
  
  private String atomFeedUrl;
  private String rssFeedUrl;
}