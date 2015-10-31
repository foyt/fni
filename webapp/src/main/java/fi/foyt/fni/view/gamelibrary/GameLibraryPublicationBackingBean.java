package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/{urlName}", to = "/gamelibrary/publication.jsf")
public class GameLibraryPublicationBackingBean {
  
  @Parameter
  @Matches ("[a-zA-Z0-9_.-]{1,}")
  private String urlName;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private PublicationController publicationController;

  @Inject
  private NavigationController navigationController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private GameLibraryTagController gameLibraryTagController;

  @Inject
  private ForumController forumController;

  @Inject
  private SessionShoppingCartController sessionShoppingCartController;
  
  @RequestAction
  public String init() {
    publication = publicationController.findPublicationByUrlName(getUrlName());
    if (publication == null) {
      return navigationController.notFound();
    }
    
    if (!publication.getPublished()) {
      if (!sessionController.isLoggedIn()) {
        throw new UnauthorizedException();
      }

      if (!publication.getCreator().getId().equals(sessionController.getLoggedUserId())) {
        if (!sessionController.hasLoggedUserRole(Role.GAME_LIBRARY_MANAGER)) {
          return navigationController.accessDenied();
        }
      }
    }
    
    if (StringUtils.isNotBlank(publication.getDescriptionPlain())) {
      metaDescription = StringEscapeUtils.escapeHtml4(publication.getDescriptionPlain());
    } else {
      metaDescription = "";
    }
    
    metaTitle = publication.getName();
    metaUrl = String.format("%s/gamelibrary/%s", systemSettingsController.getSiteUrl(true, true), publication.getUrlName());
    metaImage = String.format("%s/gamelibrary/publicationImages/%d", systemSettingsController.getSiteUrl(true, true), publication.getDefaultImage().getId());
    metaLocale = publication.getLanguage() != null ? publication.getLanguage().getLocale().toString() : "";
    
    String description = publication.getDescription();
    List<PublicationImage> images = publicationController.listPublicationImagesByPublication(publication);
    List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
    List<PublicationAuthor> publicationAuthors = publicationController.listPublicationAuthors(publication);
    tags = new ArrayList<>();
    authors = new ArrayList<>();
    
    for (PublicationTag publicationTag : publicationTags) {
      tags.add(publicationTag.getTag().getText());
    }

    for (PublicationAuthor publicationAuthor : publicationAuthors) {
      authors.add(publicationAuthor.getAuthor());
    }
    
    this.hasImage = !images.isEmpty();
    this.description = StringUtils.isBlank(description) ? "" : description.replace("\n", "<br/>");
    this.creativeCommonsLicence = CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
    this.commentCount = publication.getForumTopic() != null ? forumController.countPostsByTopic(publication.getForumTopic()) : null;
    
    if (publication instanceof BookPublication) {
      downloadable = ((BookPublication) publication).getDownloadableFile() != null;
      purchasable = ((BookPublication) publication).getPrintableFile() != null;
      pageNumbers = ((BookPublication) publication).getNumberOfPages();
    }
    
    return null;
  }
  
	public Publication getPublication() {
		return publication;
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	public String getMetaDescription() {
    return metaDescription;
  }
	
	public String getMetaImage() {
    return metaImage;
  }
	
	public String getMetaLocale() {
    return metaLocale;
  }
	
	public String getMetaTitle() {
    return metaTitle;
  }
	
	public String getMetaUrl() {
    return metaUrl;
  }
	
	public boolean hasImages() {
    return hasImage;
  }

  public List<String> getTags() {
    return tags;
  }
  
  public boolean getHasAuthors() {
    return getPublicationAuthors().size() > 0;
  }
  
  public boolean getHasSingleAuthor() {
    return getPublicationAuthors().size() == 1;
  }
  
  public List<User> getAuthors() {
    return getPublicationAuthors();
  }
  
  private List<User> getPublicationAuthors() {
    return authors;
  }
  
  public Long getPublicationCommentCount() {
    return commentCount;
  }
  
  public Integer getPublicationNumberOfPages() {
    return pageNumbers;
  }
  
  public boolean getPublicationDownloadable() {
    return downloadable;
  }

  public boolean getPublicationPurchasable() {
    return purchasable;
  }
  
  public CreativeCommonsLicense getCreativeCommonsLicense() {
    return creativeCommonsLicence;
  }
  
  public String getDescription() {
    return description;
  }
  
  public String addPublicationToShoppingCart() {
    sessionShoppingCartController.addPublication(publication);
    return null;
  }
	
	private Publication publication;
	private String metaDescription;
	private String metaTitle;
	private String metaUrl;
	private String metaImage;
	private String metaLocale;
  private List<User> authors;
  private Boolean hasImage;
  private List<String> tags;
  private Long commentCount;
  private Integer pageNumbers;
  private Boolean purchasable;
  private Boolean downloadable;
  private String description;
  private CreativeCommonsLicense creativeCommonsLicence;
}