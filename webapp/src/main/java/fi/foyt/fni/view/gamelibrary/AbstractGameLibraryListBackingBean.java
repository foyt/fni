package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

public abstract class AbstractGameLibraryListBackingBean {

  @Inject
  private PublicationController publicationController;

  @Inject
  private GameLibraryTagController gameLibraryTagController;

  @Inject
  private ForumController forumController;

  @Inject
  private SessionShoppingCartController sessionShoppingCartController;
  
  protected String init(List<BookPublication> publications) {
    this.publications = publications;
    this.authors = new HashMap<>();
    this.hasImages = new HashMap<>();
    this.tags = new HashMap<>();
    this.commentCounts = new HashMap<>();
    this.descriptions = new HashMap<>();
    this.creativeCommonsLicences = new HashMap<>();
    this.downloadables = new HashMap<>();
    this.purchasables = new HashMap<>();
    this.pageNumbers = new HashMap<>();
    
    for (BookPublication publication : publications) {
      String description = publication.getDescription();
      List<PublicationImage> images = publicationController.listPublicationImagesByPublication(publication);
      List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
      List<String> tags = new ArrayList<>();
      List<PublicationAuthor> authors = publicationController.listPublicationAuthors(publication);
      List<User> users = new ArrayList<User>(authors.size());
      
      for (PublicationTag publicationTag : publicationTags) {
        tags.add(publicationTag.getTag().getText());
      }

      for (PublicationAuthor publicationAuthor : authors) {
        users.add(publicationAuthor.getAuthor());
      }
      
      this.hasImages.put(publication.getId(), !images.isEmpty());
      this.tags.put(publication.getId(), tags);
      this.descriptions.put(publication.getId(), StringUtils.isBlank(description) ? "" : description.replace("\n", "<br/>"));
      this.creativeCommonsLicences.put(publication.getId(), CreativeCommonsUtils.parseLicenseUrl(publication.getLicense()));
      this.authors.put(publication.getId(), users);
      this.commentCounts.put(publication.getId(), publication.getForumTopic() != null ? forumController.countPostsByTopic(publication.getForumTopic()) : null);

      downloadables.put(publication.getId(), publication.getDownloadableFile() != null);
      purchasables.put(publication.getId(), publication.getPrintableFile() != null);
      pageNumbers.put(publication.getId(), publication.getNumberOfPages());
    }
    
    return null;
  }

  
  public List<BookPublication> getPublications() {
    return publications;
  }
  
  public boolean hasImages(Publication publication) {
    return hasImages.get(publication.getId());
  }

  public List<String> getTags(Publication publication) {
    return tags.get(publication.getId());
  }
  
  public boolean hasAuthors(Publication publication) {
    return !getPublicationAuthors(publication).isEmpty();
  }
  
  public boolean hasSingleAuthor(Publication publication) {
    return getPublicationAuthors(publication).size() == 1;
  }
  
  public List<User> getAuthors(Publication publication) {
    return getPublicationAuthors(publication);
  }
  
  private List<User> getPublicationAuthors(Publication publication) {
    return authors.get(publication.getId());
  }
  
  public Long getPublicationCommentCount(Publication publication) {
    return commentCounts.get(publication.getId());
  }
  
  public Integer getPublicationNumberOfPages(Publication publication) {
    return pageNumbers.get(publication.getId());
  }
  
  public boolean isPublicationDownloadable(Publication publication) {
    return downloadables.get(publication.getId());
  }

  public boolean isPublicationPurchasable(Publication publication) {
    return purchasables.get(publication.getId());
  }
  
  public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
    return creativeCommonsLicences.get(publication.getId());
  }
  
  public String getDescription(Publication publication) {
    return descriptions.get(publication.getId());
  }
  
  public String addPublicationToShoppingCart(Publication publication) {
    sessionShoppingCartController.addPublication(publication);
    return null;
  }
  
  private List<BookPublication> publications;
  private Map<Long, List<User>> authors;
  private Map<Long, Boolean> hasImages;
  private Map<Long, List<String>> tags;
  private Map<Long, Long> commentCounts;
  private Map<Long, Integer> pageNumbers;
  private Map<Long, Boolean> purchasables;
  private Map<Long, Boolean> downloadables;
  private Map<Long, String> descriptions;
  private Map<Long, CreativeCommonsLicense> creativeCommonsLicences;
  
}
