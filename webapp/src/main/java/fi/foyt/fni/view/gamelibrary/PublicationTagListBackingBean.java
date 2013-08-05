package fi.foyt.fni.view.gamelibrary;

import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

@SessionScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-publication-tag-list", 
		pattern = "/gamelibrary/tags/#{publicationTagListBackingBean.tag}", 
		viewId = "/gamelibrary/publicationtaglist.jsf"
  )
})
public class PublicationTagListBackingBean extends AbstractPublicationListBackingBean {
	
	@Inject
	private ProductController productController;
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@URLAction
	public void init() throws FileNotFoundException {
		gameLibraryTag = gameLibraryTagController.findTagByText(tag);
		if (gameLibraryTag == null) {
			throw new FileNotFoundException();
		}
		
		setProducts(productController.listProductsByTags(Arrays.asList(gameLibraryTag)));
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public GameLibraryTag getGameLibraryTag() {
		return gameLibraryTag;
	}

	private String tag;
	private GameLibraryTag gameLibraryTag;
}