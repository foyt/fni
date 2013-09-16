package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
public class PublicationCategoriesBackingBean {
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private SessionController sessionController;
	
	@Inject
	private PublicationController publicationController;
	
	@PostConstruct
	public void init() {
		tags = gameLibraryTagController.listGameLibraryTags();
	}
	
	public List<GameLibraryTag> getTags() {
		return tags;
	}
	
	public void setTags(List<GameLibraryTag> tags) {
		this.tags = tags;
	}
	
	private List<GameLibraryTag> tags;
}