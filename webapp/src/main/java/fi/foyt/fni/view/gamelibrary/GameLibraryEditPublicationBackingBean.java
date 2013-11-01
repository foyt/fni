package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.facelets.FaceletException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
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
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "gamelibrary-edit", 
		pattern = "/gamelibrary/manage/#{gameLibraryEditPublicationBackingBean.publicationId}/edit", 
		viewId = "/gamelibrary/editpublication.jsf"
	) 
})
public class GameLibraryEditPublicationBackingBean {

	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;
	
	@PostConstruct
	public void load() {
		if (sessionController.isLoggedIn()) {
  		licenseSelectItems = createLicenseSelectItems();
  		tagSelectItems = createTagSelectItems();
  		authorSelectItems = createAuthorSelectItems();
  		creativeCommonsDerivativesSelectItems = createCreativeCommonsDerivativesSelectItems();
  		creativeCommonsCommercialSelectItems = createCreativeCommsonCommercialSelectItems();

  		if (tagSelectItems.size() > 0 && tagSelectItems.get(1).getSelectItems().length > 0) {
        addExistingTag = (String) tagSelectItems.get(1).getSelectItems()[0].getValue();
      }
		}
	}
	
	@URLAction (onPostback = false)
	@LoggedIn
	@Secure(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void init() {
		Publication publication = publicationController.findPublicationById(publicationId);
		
		name = publication.getName();
		description = publication.getDescription();
		price = publication.getPrice();
		requiresDelivery = publication.getRequiresDelivery();
		purchasable = publication.getPurchasable();
		weight = publication.getWeight();
		width = publication.getWidth();
		height = publication.getHeight();
		depth = publication.getDepth();
		tags = new ArrayList<>();
		
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
		
		if (publication instanceof BookPublication) {
			BookPublication bookPublication = (BookPublication) publication;
			downloadable = bookPublication.getDownloadable();
			numberOfPages = bookPublication.getNumberOfPages();
		}
		
		CreativeCommonsLicense creativeCommonsLicense = CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
		licenseType =	creativeCommonsLicense != null ? LicenseType.CREATIVE_COMMONS : LicenseType.OTHER;
		switch (licenseType) {
			case CREATIVE_COMMONS:
				creativeCommonsDerivatives = creativeCommonsLicense.getDerivatives() 
				  ? creativeCommonsLicense.getShareAlike() 
				  		? CreativeCommonsDerivatives.SHARE_ALIKE : CreativeCommonsDerivatives.YES 
				  : CreativeCommonsDerivatives.NO;
				creativeCommonsCommercial = creativeCommonsLicense.getCommercial() ? CreativeCommonsCommercial.YES : CreativeCommonsCommercial.NO;
				licenseOther = "";
			break;
			case OTHER:
				creativeCommonsDerivatives = CreativeCommonsDerivatives.YES;
				creativeCommonsCommercial = CreativeCommonsCommercial.YES;
				licenseOther = publication.getLicense();
			break;
		}
	}
	
	public Long getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(Long publicationId) {
		this.publicationId = publicationId;
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

	public Boolean getRequiresDelivery() {
		return requiresDelivery;
	}

	public void setRequiresDelivery(Boolean requiresDelivery) {
		this.requiresDelivery = requiresDelivery;
	}

	public Boolean getDownloadable() {
		return downloadable;
	}

	public void setDownloadable(Boolean downloadable) {
		this.downloadable = downloadable;
	}

	public Boolean getPurchasable() {
		return purchasable;
	}

	public void setPurchasable(Boolean purchasable) {
		this.purchasable = purchasable;
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
	
	public LicenseType getLicenseType() {
		return licenseType;
	}
	
	public void setLicenseType(LicenseType licenseType) {
		this.licenseType = licenseType;
	}
	
	public CreativeCommonsDerivatives getCreativeCommonsDerivatives() {
		return creativeCommonsDerivatives;
	}
	
	public void setCreativeCommonsDerivatives(CreativeCommonsDerivatives creativeCommonsDerivatives) {
		this.creativeCommonsDerivatives = creativeCommonsDerivatives;
	}
	
	public CreativeCommonsCommercial getCreativeCommonsCommercial() {
		return creativeCommonsCommercial;
	}
	
	public void setCreativeCommonsCommercial(CreativeCommonsCommercial creativeCommonsCommercial) {
		this.creativeCommonsCommercial = creativeCommonsCommercial;
	}
	
	public String getLicenseOther() {
		return licenseOther;
	}
	
	public void setLicenseOther(String licenseOther) {
		this.licenseOther = licenseOther;
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
	
	public List<SelectItem> getLicenseSelectItems() {
		return licenseSelectItems;
	}
	
	public List<SelectItemGroup> getTagSelectItems() {
		return tagSelectItems;
	}
	
	public List<SelectItem> getAuthorSelectItems() {
		return authorSelectItems;
	}
	
	public List<SelectItem> getCreativeCommonsCommercialSelectItems() {
		return creativeCommonsCommercialSelectItems;
	}
	
	public List<SelectItem> getCreativeCommonsDerivativesSelectItems() {
		return creativeCommonsDerivativesSelectItems;
	}
	
	public void addTag() {
		String tag = getAddExistingTag();
		if ("_NEW_".equals(tag)) {
		  tag = StringUtils.lowerCase(StringUtils.trim(getAddNewTag()));
		}
		
		if (StringUtils.isNotBlank(tag)) {
			if (!tags.contains(tag)) {
				tags.add(tag);
			}
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
		  String license = null;
		  
		  switch (getLicenseType()) {
				case CREATIVE_COMMONS:
					boolean derivatives = getCreativeCommonsDerivatives() != CreativeCommonsDerivatives.NO;
					boolean shareAlike = getCreativeCommonsDerivatives() == CreativeCommonsDerivatives.SHARE_ALIKE;
					boolean commercial = getCreativeCommonsCommercial() == CreativeCommonsCommercial.YES;
					license = CreativeCommonsUtils.createLicenseUrl(true, derivatives, shareAlike, commercial);
				break;
				case OTHER:
					license = getLicenseOther();
				break;
			}
		  
		  List<GameLibraryTag> tags = new ArrayList<>();
		  List<User> authors = new ArrayList<User>();
		  
		  for (String tag : getTags()) {
		  	GameLibraryTag gameLibraryTag = gameLibraryTagController.findTagByText(tag);
		  	if (gameLibraryTag == null) {
		  		gameLibraryTag = gameLibraryTagController.createTag(tag);
		  	}
		  	tags.add(gameLibraryTag);
		  }
		  
		  for (Long authorId : getAuthorIds()) {
		  	User author = userController.findUserById(authorId);
		  	authors.add(author);
		  }
		  
			publicationController.updateName(bookPublication, getName());
			publicationController.updateDescription(bookPublication, getDescription());
			publicationController.updatePrice(bookPublication, getPrice());
			publicationController.updateRequiresDelivery(bookPublication, getRequiresDelivery());
			publicationController.updatePurchasable(bookPublication, getPurchasable());
			publicationController.updateWeight(publication, getWeight());
			publicationController.updateDimensions(publication, getWidth(), getHeight(), getDepth());
			publicationController.updatePublicationAuthors(publication, authors);
			publicationController.updateLicense(publication, license);
			publicationController.updateTags(bookPublication, tags);
			
			publicationController.updateNumberOfPages(bookPublication, getNumberOfPages());
			publicationController.updateDownloadable(bookPublication, getDownloadable());
			publicationController.updatedModified(bookPublication, sessionController.getLoggedUser(), new Date());
		} else {
			throw new FaceletException("Not implemented");
		}
	}
	
	private List<SelectItem> createLicenseSelectItems() {
		List<SelectItem> licenseSelectItems = new ArrayList<>();
		licenseSelectItems.add(new SelectItem(LicenseType.CREATIVE_COMMONS, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseCreativeCommons")));
		licenseSelectItems.add(new SelectItem(LicenseType.OTHER, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseOther")));
		return licenseSelectItems;
	}
	
	private List<SelectItemGroup> createTagSelectItems() {
		List<GameLibraryTag> tags = gameLibraryTagController.listGameLibraryTags();
		
		ArrayList<SelectItemGroup> result = new ArrayList<>();

		List<SelectItem> tagItems = new ArrayList<>();
		for (GameLibraryTag tag : tags) {
			tagItems.add(new SelectItem(tag.getText(), StringUtils.capitalize(tag.getText())));
		}
		
		SelectItemGroup existingTagGroup = new SelectItemGroup(FacesUtils.getLocalizedValue("gamelibrary.editPublication.existingTagsGroup"), "", false, tagItems.toArray(new SelectItem[0]));
		SelectItemGroup newTagGroup = new SelectItemGroup(FacesUtils.getLocalizedValue("gamelibrary.editPublication.createTagGroup"), "", false, new SelectItem[] { new SelectItem("_NEW_", FacesUtils.getLocalizedValue("gamelibrary.editPublication.createTagItem")) });

		result.add(newTagGroup);
		result.add(existingTagGroup);

		return result;
	}
	
	private List<SelectItem> createAuthorSelectItems() {
		List<SelectItem> result = new ArrayList<>(); 
		User loggedUser = sessionController.getLoggedUser();

		result.add(new SelectItem(loggedUser.getId(), loggedUser.getFullName()));
		List<User> users = userController.listUserFriends(loggedUser);
		for (User user : users) {
			result.add(new SelectItem(user.getId(), user.getFullName()));
		}
		
		return result;
	}

	private List<SelectItem> createCreativeCommonsDerivativesSelectItems() {
		List<SelectItem> result = new ArrayList<>(); 
		
		result.add(new SelectItem(CreativeCommonsDerivatives.YES, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseCreativeCommonsAllowModificationsYes")));
		result.add(new SelectItem(CreativeCommonsDerivatives.NO, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseCreativeCommonsAllowModificationsNo")));
		result.add(new SelectItem(CreativeCommonsDerivatives.SHARE_ALIKE, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseCreativeCommonsAllowModificationsShare")));

		return result;
	}
	
	private List<SelectItem> createCreativeCommsonCommercialSelectItems() {
		List<SelectItem> result = new ArrayList<>(); 
	
		result.add(new SelectItem(CreativeCommonsCommercial.YES, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseCreativeCommonsAllowCommercialYes")));
		result.add(new SelectItem(CreativeCommonsCommercial.NO, FacesUtils.getLocalizedValue("gamelibrary.editPublication.licenseCreativeCommonsAllowCommercialNo")));

		return result;
	}
	
	private Long publicationId;
	private String name;
	private String description;
	private Double price;
	private Boolean requiresDelivery;
	private Boolean downloadable;
	private Boolean purchasable;
	private Double weight;
	private Integer width;
	private Integer height;
	private Integer depth;
	private Integer numberOfPages;
	private LicenseType licenseType;
	private CreativeCommonsDerivatives creativeCommonsDerivatives;
	private CreativeCommonsCommercial creativeCommonsCommercial;
	private String licenseOther;
	private List<String> tags;
	private String addExistingTag;
	private String addNewTag;
	private String removeTagText;
	private Long addAuthorId;
	private Long removeAuthorId;
	private List<Long> authorIds;
	private List<String> authorNames;

	private List<SelectItem> creativeCommonsDerivativesSelectItems;
	private List<SelectItem> creativeCommonsCommercialSelectItems;
	private List<SelectItem> licenseSelectItems;
 	private List<SelectItemGroup> tagSelectItems;
 	private List<SelectItem> authorSelectItems;
 	
 	public enum LicenseType {
		CREATIVE_COMMONS,
		OTHER
	}
 	
 	public enum CreativeCommonsDerivatives {
 		YES,
 		NO,
 		SHARE_ALIKE
 	}
 	
 	public enum CreativeCommonsCommercial {
 		YES,
 		NO
 	}
}
