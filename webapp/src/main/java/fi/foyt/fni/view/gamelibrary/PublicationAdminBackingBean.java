package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.gamelibrary.PublicationController;

@Stateful
@RequestScoped
@Named
public class PublicationAdminBackingBean {
	
	@Inject
	private PublicationController publicationController;

	public void publish() throws IOException {
		publicationController.publishProduct(publicationController.findProductById(publicationId));
		
		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/")
  	  .toString());
	}

	public void unpublish() throws IOException {
		publicationController.unpublishProduct(publicationController.findProductById(publicationId));

		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/unpublished/")
  	  .toString());
	}

	public void delete() throws IOException {
		publicationController.deleteProduct(publicationController.findProductById(publicationId));

		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/unpublished/")
  	  .toString());
	}
	
	public Long getPublicationId() {
		return publicationId;
	}
	
	public void setPublicationId(Long publicationId) {
		this.publicationId = publicationId;
	}
	
	private Long publicationId;
}
