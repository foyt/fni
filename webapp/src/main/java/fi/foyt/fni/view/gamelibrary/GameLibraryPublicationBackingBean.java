package fi.foyt.fni.view.gamelibrary;

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
		id = "gamelibrary-publication", 
		pattern = "/gamelibrary/#{gameLibraryPublicationBackingBean.urlName}", 
		viewId = "/gamelibrary/publication.jsf"
  )
})
public class GameLibraryPublicationBackingBean {
  
	@Inject
	private PublicationController publicationController;
	
	public Publication getPublication() {
		return publicationController.findPublicationByUrlName(getUrlName());
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	private String urlName;
}