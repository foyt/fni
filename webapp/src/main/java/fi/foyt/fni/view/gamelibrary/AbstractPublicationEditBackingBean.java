package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

public class AbstractPublicationEditBackingBean {

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

	public List<SelectItemGroup> getTagSelectItems() {
		return tagSelectItems;
	}

	public void setTagSelectItems(List<SelectItemGroup> tagSelectItems) {
		this.tagSelectItems = tagSelectItems;
	}

	public Integer getBookNumberOfPages() {
		return bookNumberOfPages;
	}

	public void setBookNumberOfPages(Integer bookNumberOfPages) {
		this.bookNumberOfPages = bookNumberOfPages;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
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
	private String bookAuthor;
	private List<SelectItemGroup> tagSelectItems;
}
