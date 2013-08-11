package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

public class AbstractPublicationEditBackingBean {
	
	@Inject
	private SessionController sessionController;
	
	@Inject
	private UserController userController;

	protected List<SelectItemGroup> createTagSelectItems(List<GameLibraryTag> tags) {
		ArrayList<SelectItemGroup> result = new ArrayList<>();

		List<SelectItem> tagItems = new ArrayList<>();
		for (GameLibraryTag tag : tags) {
			tagItems.add(new SelectItem(tag.getText(), StringUtils.capitalize(tag.getText())));
		}

		SelectItemGroup existingTagGroup = new SelectItemGroup("Existing Tags", "", false, tagItems.toArray(new SelectItem[0]));
		SelectItemGroup newTagGroup = new SelectItemGroup("Create New Tag", "", false, new SelectItem[] { new SelectItem("new", "New Tag") });

		result.add(newTagGroup);
		result.add(existingTagGroup);

		return result;
	}
	
	protected List<SelectItem> createLicenseSelectItems() {
		List<SelectItem> result = new ArrayList<>(); 
		result.add(new SelectItem("CC", "Creative Commons 3.0"));
		result.add(new SelectItem("OTHER", "Other"));
		return result;
	}
	
	protected List<SelectItem> createAuthorSelectItems() {
		List<SelectItem> result = new ArrayList<>(); 
		User loggedUser = sessionController.getLoggedUser();

		result.add(new SelectItem(null, "-- Select --"));
		result.add(new SelectItem(loggedUser.getId(), loggedUser.getFullName()));
		List<User> users = userController.listUserFriends(loggedUser);
		for (User user : users) {
			result.add(new SelectItem(user.getId(), user.getFullName()));
		}
		
		return result;
	}

	public List<SelectItemGroup> getTagSelectItems() {
		return tagSelectItems;
	}

	public void setTagSelectItems(List<SelectItemGroup> tagSelectItems) {
		this.tagSelectItems = tagSelectItems;
	}
	
	public List<SelectItem> getLicenseSelectItems() {
		return licenseSelectItems;
	}
	
	public void setLicenseSelectItems(List<SelectItem> licenseSelectItems) {
		this.licenseSelectItems = licenseSelectItems;
	}
	
	public List<SelectItem> getAuthorSelectItems() {
		return authorSelectItems;
	}
	
	public void setAuthorSelectItems(List<SelectItem> authorSelectItems) {
		this.authorSelectItems = authorSelectItems;
	}

	public Integer getBookNumberOfPages() {
		return bookNumberOfPages;
	}

	public void setBookNumberOfPages(Integer bookNumberOfPages) {
		this.bookNumberOfPages = bookNumberOfPages;
	}
	
	public Long getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(Long publicationId) {
		this.publicationId = publicationId;
	}

	public String getPublicationName() {
		return publicationName;
	}

	public void setPublicationName(String publicationName) {
		this.publicationName = publicationName;
	}

	public String getPublicationDescription() {
		return publicationDescription;
	}

	public void setPublicationDescription(String publicationDescription) {
		this.publicationDescription = publicationDescription;
	}

	public Double getPublicationPrice() {
		return publicationPrice;
	}

	public void setPublicationPrice(Double publicationPrice) {
		this.publicationPrice = publicationPrice;
	}

	public Part getPublicationFile() {
		return publicationFile;
	}

	public void setPublicationFile(Part publicationFile) {
		this.publicationFile = publicationFile;
	}

	public String getPublicationTags() {
		return publicationTags;
	}

	public void setPublicationTags(String publicationTags) {
		this.publicationTags = publicationTags;
	}

	public Boolean getPublicationRequiresDelivery() {
		return publicationRequiresDelivery;
	}

	public void setPublicationRequiresDelivery(Boolean publicationRequiresDelivery) {
		this.publicationRequiresDelivery = publicationRequiresDelivery;
	}

	public Boolean getPublicationDownloadable() {
		return publicationDownloadable;
	}

	public void setPublicationDownloadable(Boolean publicationDownloadable) {
		this.publicationDownloadable = publicationDownloadable;
	}

	public Boolean getPublicationPurchasable() {
		return publicationPurchasable;
	}

	public void setPublicationPurchasable(Boolean publicationPurchasable) {
		this.publicationPurchasable = publicationPurchasable;
	}

	public Double getPublicationWeight() {
		return publicationWeight;
	}

	public void setPublicationWeight(Double publicationWeight) {
		this.publicationWeight = publicationWeight;
	}

	public Integer getPublicationWidth() {
		return publicationWidth;
	}

	public void setPublicationWidth(Integer publicationWidth) {
		this.publicationWidth = publicationWidth;
	}

	public Integer getPublicationHeight() {
		return publicationHeight;
	}

	public void setPublicationHeight(Integer publicationHeight) {
		this.publicationHeight = publicationHeight;
	}

	public Integer getPublicationDepth() {
		return publicationDepth;
	}

	public void setPublicationDepth(Integer publicationDepth) {
		this.publicationDepth = publicationDepth;
	}
	
	public String getLicenseType() {
		return licenseType;
	}
	
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
	
	public String getCreativeCommonsCommercial() {
		return creativeCommonsCommercial;
	}
	
	public void setCreativeCommonsCommercial(String creativeCommonsCommercial) {
		this.creativeCommonsCommercial = creativeCommonsCommercial;
	}
	
	public String getCreativeCommonsDerivatives() {
		return creativeCommonsDerivatives;
	}
	
	public void setCreativeCommonsDerivatives(String creativeCommonsDerivatives) {
		this.creativeCommonsDerivatives = creativeCommonsDerivatives;
	}
	
	public String getLicenseOther() {
		return licenseOther;
	}
	
	public void setLicenseOther(String licenseOther) {
		this.licenseOther = licenseOther;
	}
	
	public String getAuthorIds() {
		return authorIds;
	}
	
	public void setAuthorIds(String authorIds) {
		this.authorIds = authorIds;
	}
	
	public List<User> getAuthors() {
		return authors;
	}
	
	public void setAuthors(List<User> authors) {
		this.authors = authors;
	}
	
	protected String getLicenseUrl() {
		if ("CC".equals(getLicenseType())) {
			boolean attribution = true;
			boolean shareAlike = false;
			boolean derivatives = false;
			boolean commercial = true;
			
			if ("YES".equals(getCreativeCommonsDerivatives())) {
				derivatives = true;
			} else if ("SHARE_ALIKE".equals(getCreativeCommonsDerivatives())) {
				derivatives = true;
				shareAlike = true;
			}
			
			if ("NO".equals(getCreativeCommonsCommercial())) {
				commercial = false;
			}

			return CreativeCommonsUtils.createLicenseUrl(attribution, derivatives, shareAlike, commercial);
		} else {
			return getLicenseOther();
		}
	}

	private Long publicationId;
	private String publicationName;
	private String publicationDescription;
	private Double publicationPrice;
	private Part publicationFile;
	private String publicationTags;
	private Boolean publicationRequiresDelivery;
	private Boolean publicationDownloadable;
	private Boolean publicationPurchasable;
	private Double publicationWeight;
	private Integer publicationWidth;
	private Integer publicationHeight;
	private Integer publicationDepth;
	private Integer bookNumberOfPages;
	private String licenseType;
	private String creativeCommonsDerivatives;
	private String creativeCommonsCommercial;
	private String licenseOther;
	private String authorIds;
	private List<User> authors;
	private List<SelectItemGroup> tagSelectItems;
	private List<SelectItem> licenseSelectItems;
	private List<SelectItem> authorSelectItems;
}
