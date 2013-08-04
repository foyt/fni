package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class BookProduct extends FileProduct {

	public Boolean getDownloadable() {
		return downloadable;
	}
	
	public void setDownloadable(Boolean downloadable) {
		this.downloadable = downloadable;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Integer getNumberOfPages() {
		return numberOfPages;
	}
	
	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
  @Column (nullable=false, columnDefinition = "BIT")
  @NotNull
	private Boolean downloadable;
  
  private String author;
  
  private Integer numberOfPages;
}
