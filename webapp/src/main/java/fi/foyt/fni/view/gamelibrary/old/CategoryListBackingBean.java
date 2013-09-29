package fi.foyt.fni.view.gamelibrary.old;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

//@Stateful
//@RequestScoped
//@Named
public class CategoryListBackingBean {
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	public List<GameLibraryTag> getTagsWithPublishedPublications() {
		return gameLibraryTagController.listActiveGameLibraryTags();
	}
	
}
