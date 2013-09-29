package fi.foyt.fni.view.gamelibrary.old;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;

//@RequestScoped
//@Named
//@Stateful
//@URLMappings(mappings = {
//  @URLMapping(
//		id = "gamelibrary-publication", 
//		pattern = "/gamelibrary/#{publicationDetailsBackingBean.urlName}", 
//		viewId = "/gamelibrary/publicationdetails.jsf"
//  )
//})
public class PublicationDetailsBackingBean {
	
	@Inject
	private PublicationController publicationController;
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ForumController forumController;

	@Inject
	private SessionShoppingCartController sessionShoppingCartController;

	@Inject
	private SessionController sessionController;

	@URLAction (onPostback = true)
	public void init() throws FileNotFoundException {
		Publication publication = publicationController.findPublicationByUrlName(getUrlName());
		if (publication == null) {
			throw new FileNotFoundException();
		}
		
		id = publication.getId();
		name = publication.getName();
		description = publication.getDescription();
		license = publication.getLicense();
		price = publication.getPrice();
		tags = gameLibraryTagController.listPublicationGameLibraryTags(publication);
		creativeCommonsLicense = CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
		hasSeveralImages = publicationController.listPublicationImagesByPublication(publication).size() > 1;
		hasImages = publicationController.listPublicationImagesByPublication(publication).size() > 0;
		defaultImage = publication.getDefaultImage();
		
		if (publication instanceof BookPublication) {
			BookPublication bookPublication = ((BookPublication) publication);
			numberOfPages = bookPublication.getNumberOfPages();
			downloadable = bookPublication.getDownloadable() && bookPublication.getFile() != null;
		}
		
		ForumTopic forumTopic = publication.getForumTopic();
		if (forumTopic != null) {
			forumTopicPath = forumTopic.getFullPath();
			commentCount = forumController.countPostsByTopic(forumTopic);
		}
		
		authors = new ArrayList<>(); 

		List<PublicationAuthor> publicationAuthors = publicationController.listPublicationAuthors(publication);
		for (PublicationAuthor publicationAuthor : publicationAuthors) {
			authors.add(publicationAuthor.getAuthor());
		}
		
		publicationImages = publicationController.listPublicationImagesByPublication(publication);
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLicense() {
		return license;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public List<GameLibraryTag> getTags() {
		return tags;
	}
	
	public Integer getNumberOfPages() {
		return numberOfPages;
	}
	
	public Boolean getDownloadable() {
		return downloadable;
	}
	
	public String getForumTopicPath() {
		return forumTopicPath;
	}
	
	public Long getCommentCount() {
		return commentCount;
	}

	public boolean getHasSeveralImages() {
		return hasSeveralImages;
	}

	public boolean getHasImages() {
		return hasImages;
	}
	
	public List<PublicationImage> getPublicationImages() {
		return publicationImages;
	}
	
	public List<User> getAuthors() {
		return authors;
	}
	
	public PublicationImage getDefaultImage() {
		return defaultImage;
	}

	public void addPublicationToShoppingCart() {
		Publication publication = publicationController.findPublicationById(getId());
		sessionShoppingCartController.addPublication(publication);
	}
	
	public CreativeCommonsLicense getCreativeCommonsLicense() {
		return creativeCommonsLicense;
	}
	
	public boolean getMayManagePublications() {
		if (sessionController.isLoggedIn()) {
			return sessionController.hasLoggedUserPermission(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS);
		} 

		return false;
	}

	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void unpublish() throws IOException {
		Publication publication = publicationController.findPublicationById(getId());
		publicationController.unpublishPublication(publication);

		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/manage/")
  	  .toString());
	}

	private Long id;
	private String name;
	private String description;
	private String license;
	private Double price;
	private Integer numberOfPages;
	private Boolean downloadable;
	private String forumTopicPath;
	private Long commentCount;
	private String urlName;
	private List<GameLibraryTag> tags;
	private List<User> authors;
	private Boolean hasSeveralImages;
	private Boolean hasImages;
	private PublicationImage defaultImage;
	private CreativeCommonsLicense creativeCommonsLicense;
	private List<PublicationImage> publicationImages;
}