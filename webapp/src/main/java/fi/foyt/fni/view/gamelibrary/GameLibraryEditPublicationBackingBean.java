package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.facelets.FaceletException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/manage/{publicationId}/edit", to = "/gamelibrary/editpublication.jsf")
@LoggedIn
@Secure(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
@SuppressWarnings ("squid:S3306")
public class GameLibraryEditPublicationBackingBean {

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long publicationId;

	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;

  @Inject
	private UserController userController;

  @Inject
	private SystemSettingsController systemSettingsController;
  
  @Inject
  private NavigationController navigationController;
  
  private Long languageId;
  private String name;
  private String description;
  private Double price;
  private Double weight;
  private Integer width;
  private Integer height;
  private Integer depth;
  private Integer numberOfPages;
  private String license;
  private List<String> tags;
  private String addExistingTag;
  private String addNewTag;
  private String removeTagText;
  private Long addAuthorId;
  private Long removeAuthorId;
  private List<Long> authorIds;
  private List<String> authorNames;
  private List<SelectItemGroup> tagSelectItems;
  private List<SelectItem> authorSelectItems;
  private List<SelectItem> languageSelectItems;
	
  @RequestAction
	@Deferred
	public String load() {
    BookPublication publication = publicationController.findBookPublicationById(publicationId);
    if (publication == null) {
      return navigationController.notFound();
    }
    
		tagSelectItems = createTagSelectItems();
		authorSelectItems = createAuthorSelectItems();
		languageSelectItems = createLanguageSelectItems();
		
		if (tagSelectItems.isEmpty() && tagSelectItems.get(1).getSelectItems().length > 0) {
      addExistingTag = (String) tagSelectItems.get(1).getSelectItems()[0].getValue();
    }
		
		return null;
	}

  @RequestAction
  @Deferred
  @IgnorePostback
	public void init() {
		BookPublication publication = publicationController.findBookPublicationById(publicationId);
		
		name = publication.getName();
		description = publication.getDescription();
		price = publication.getPrice();
		weight = publication.getWeight();
		width = publication.getWidth();
		height = publication.getHeight();
		depth = publication.getDepth();
		tags = new ArrayList<>();
		languageId = publication.getLanguage().getId();
		
		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			tags.add(publicationTag.getTag().getText());
		}
		
		authorIds = new ArrayList<>();
		authorNames = new ArrayList<>();
		List<PublicationAuthor> authors = publicationController.listPublicationAuthors(publication);
		for (PublicationAuthor author : authors) {
			authorIds.add(author.getAuthor().getId());
			authorNames.add(author.getAuthor().getFullName());
		}
		
		numberOfPages = publication.getNumberOfPages();
	
		license = publication.getLicense();
	}
  
  private List<SelectItem> createLanguageSelectItems() {
    List<SelectItem> result = new ArrayList<>();
    
    List<Language> languages = systemSettingsController.listLanguages();
    for (Language language : languages) {
      result.add(new SelectItem(language.getId(), FacesUtils.getLocalizedValue("generic.languages." + language.getISO3())));
    }
    
    return result;
  }
	
	public Long getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(Long publicationId) {
		this.publicationId = publicationId;
	}
	
	public Long getLanguageId() {
    return languageId;
  }
	
	public void setLanguageId(Long languageId) {
    this.languageId = languageId;
  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
	public String getLicense() {
    return license;
  }
	
	public void setLicense(String license) {
    this.license = license;
  }
	
	public List<String> getTags() {
		return tags;
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public String getAddExistingTag() {
		return addExistingTag;
	}
	
	public void setAddExistingTag(String addExistingTag) {
		this.addExistingTag = addExistingTag;
	}
	
	public String getAddNewTag() {
		return addNewTag;
	}
	
	public void setAddNewTag(String addNewTag) {
		this.addNewTag = addNewTag;
	}
	
	public String getRemoveTagText() {
		return removeTagText;
	}
	
	public void setRemoveTagText(String removeTagText) {
		this.removeTagText = removeTagText;
	}
	
	public List<Long> getAuthorIds() {
		return authorIds;
	}
	
	public void setAuthorIds(List<Long> authorIds) {
		this.authorIds = authorIds;
	}
	
	public List<String> getAuthorNames() {
		return authorNames;
	}
	
	public void setAuthorNames(List<String> authorNames) {
		this.authorNames = authorNames;
	}
	
	public Long getAddAuthorId() {
		return addAuthorId;
	}
	
	public void setAddAuthorId(Long addAuthorId) {
		this.addAuthorId = addAuthorId;
	}
	
	public Long getRemoveAuthorId() {
		return removeAuthorId;
	}
	
	public void setRemoveAuthorId(Long removeAuthorId) {
		this.removeAuthorId = removeAuthorId;
	}
	
	public List<SelectItemGroup> getTagSelectItems() {
		return tagSelectItems;
	}
	
	public List<SelectItem> getAuthorSelectItems() {
		return authorSelectItems;
	}
	
	public List<SelectItem> getLanguageSelectItems() {
    return languageSelectItems;
  }
	
	public void addTag() {
		String tag = getAddExistingTag();
		if ("_NEW_".equals(tag)) {
		  tag = StringUtils.lowerCase(StringUtils.trim(getAddNewTag()));
		}
		
		if (StringUtils.isNotBlank(tag) && (!tags.contains(tag))) {
			tags.add(tag);
		}
	}
	
	public void removeTag() {
		tags.remove(getRemoveTagText());
	}
	
	public void addAuthor() {
		Long authorId = getAddAuthorId();
		authorIds.add(authorId);
		authorNames.add(userController.findUserById(authorId).getFullName());
	}
	
	public void removeAuthor() {
		int index = this.authorIds.indexOf(getRemoveAuthorId());
		
		this.authorIds.remove(index);
		this.authorNames.remove(index);
	}
	
	public void save() {
		Publication publication = publicationController.findPublicationById(publicationId);
		if (publication instanceof BookPublication) {
		  BookPublication bookPublication = (BookPublication) publication;
		  
		  List<GameLibraryTag> publicationTags = new ArrayList<>(getTags().size());
		  List<User> authors = new ArrayList<>();
		  
		  for (String tag : getTags()) {
		  	GameLibraryTag gameLibraryTag = gameLibraryTagController.findTagByText(tag);
		  	if (gameLibraryTag == null) {
		  		gameLibraryTag = gameLibraryTagController.createTag(tag);
		  	}
		  	
		  	publicationTags.add(gameLibraryTag);
		  }
		  
		  for (Long authorId : getAuthorIds()) {
		  	User author = userController.findUserById(authorId);
		  	authors.add(author);
		  }
		  
		  Language language = systemSettingsController.findLanguageById(languageId);
		  
			publicationController.updateName(bookPublication, getName());
			publicationController.updateDescription(bookPublication, getDescription());
			publicationController.updatePrice(bookPublication, getPrice());
			publicationController.updateWeight(publication, getWeight());
			publicationController.updateDimensions(publication, getWidth(), getHeight(), getDepth());
			publicationController.updatePublicationAuthors(publication, authors);
			publicationController.updateLicense(bookPublication, getLicense());
			publicationController.updateTags(bookPublication, publicationTags);
			publicationController.updateNumberOfPages(bookPublication, getNumberOfPages());
      publicationController.updatePublicationLanguage(bookPublication, language);
			publicationController.updatedModified(bookPublication, sessionController.getLoggedUser(), new Date());
		} else {
			throw new FaceletException("Not implemented");
		}
	}
	
	private List<SelectItemGroup> createTagSelectItems() {
		List<GameLibraryTag> gameLibraryTags = gameLibraryTagController.listGameLibraryTags();
		
		ArrayList<SelectItemGroup> result = new ArrayList<>();

		List<SelectItem> tagItems = new ArrayList<>();
		for (GameLibraryTag tag : gameLibraryTags) {
			tagItems.add(new SelectItem(tag.getText(), StringUtils.capitalize(tag.getText())));
		}
		
		SelectItemGroup existingTagGroup = new SelectItemGroup(FacesUtils.getLocalizedValue("gamelibrary.editPublication.existingTagsGroup"), "", false, tagItems.toArray(new SelectItem[0]));
		SelectItemGroup newTagGroup = new SelectItemGroup(FacesUtils.getLocalizedValue("gamelibrary.editPublication.createTagGroup"), "", false, new SelectItem[] { new SelectItem("_NEW_", FacesUtils.getLocalizedValue("gamelibrary.editPublication.createTagItem")) });

		result.add(newTagGroup);
		result.add(existingTagGroup);

		return result;
	}
	
  private List<SelectItem> createAuthorSelectItems() {
    List<User> users = new ArrayList<>(userController.listUsersSortedByName());
		List<SelectItem> result = new ArrayList<>(users.size()); 
		
		Collections.sort(users, new NullFullNameComparator());
		
		for (User user : users) {
		  result.add(new SelectItem(user.getId(), userController.getUserDisplayNameWithMail(user, true)));
		}
		
		return result;
	}
  
  private class NullFullNameComparator implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {
      String fullName1 = o1.getFullName();
      String fullName2 = o2.getFullName();
      
      if (fullName1 == fullName2) {
        return 0;
      }
      
      if (fullName1 == null) {
        return 1;
      }
      
      if (fullName2 == null) {
        return -1;
      }
      
      return 0;
    }
    
  }

}
