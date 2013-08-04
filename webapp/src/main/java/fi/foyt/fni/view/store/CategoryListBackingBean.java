package fi.foyt.fni.view.store;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.gamelibrary.StoreTagController;
import fi.foyt.fni.persistence.model.gamelibrary.StoreTag;

@Stateful
@RequestScoped
@Named
public class CategoryListBackingBean {
	
	@Inject
	private StoreTagController tagController;
	
	public List<StoreTag> getTagsWithPublishedProducts() {
		return tagController.listActiveStoreTags();
	}
	
}
