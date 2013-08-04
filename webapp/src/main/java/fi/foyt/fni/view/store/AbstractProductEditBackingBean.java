package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.gamelibrary.StoreTag;

public class AbstractProductEditBackingBean {
	
	protected List<SelectItemGroup> createTagSelectItems(List<StoreTag> tags) {
		 ArrayList<SelectItemGroup> result = new ArrayList<>();

		List<SelectItem> tagItems = new ArrayList<>();
		for (StoreTag tag : tags) {
			tagItems.add(new SelectItem(tag.getText(), StringUtils.capitalize(tag.getText())));
		}

		SelectItemGroup existingTagGroup = new SelectItemGroup("Existing Tags", "", false, tagItems.toArray(new SelectItem[0]));
		SelectItemGroup newTagGroup = new SelectItemGroup("Create New Tag", "", false, new SelectItem[] {
			new SelectItem("new", "New Tag")
		});

		result.add(newTagGroup);
		result.add(existingTagGroup);
		
		return result;
	}
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public List<SelectItemGroup> getTagSelectItems() {
		return tagSelectItems;
	}

	public void setTagSelectItems(List<SelectItemGroup> tagSelectItems) {
		this.tagSelectItems = tagSelectItems;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public Part getProductFile() {
		return productFile;
	}

	public void setProductFile(Part productFile) {
		this.productFile = productFile;
	}

	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}

	public Boolean getProductRequiresDelivery() {
		return productRequiresDelivery;
	}

	public void setProductRequiresDelivery(Boolean productRequiresDelivery) {
		this.productRequiresDelivery = productRequiresDelivery;
	}

	public Boolean getProductDownloadable() {
		return productDownloadable;
	}

	public void setProductDownloadable(Boolean productDownloadable) {
		this.productDownloadable = productDownloadable;
	}

	public Boolean getProductPurchasable() {
		return productPurchasable;
	}

	public void setProductPurchasable(Boolean productPurchasable) {
		this.productPurchasable = productPurchasable;
	}

	public Integer getProductDepth() {
		return productDepth;
	}

	public void setProductDepth(Integer productDepth) {
		this.productDepth = productDepth;
	}

	public Integer getProductHeight() {
		return productHeight;
	}

	public void setProductHeight(Integer productHeight) {
		this.productHeight = productHeight;
	}

	public Double getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(Double productWeight) {
		this.productWeight = productWeight;
	}

	public Integer getProductWidth() {
		return productWidth;
	}

	public void setProductWidth(Integer productWidth) {
		this.productWidth = productWidth;
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

	private Long productId;
	private String productName;
	private String productDescription;
	private Double productPrice;
	private Part productFile;
	private String productTags;
	private Boolean productRequiresDelivery;
	private Boolean productDownloadable;
	private Boolean productPurchasable;
	private Double productWeight;
	private Integer productWidth;
	private Integer productHeight;
	private Integer productDepth;
	private Integer bookNumberOfPages;
	private String bookAuthor;
	private List<SelectItemGroup> tagSelectItems;
}
