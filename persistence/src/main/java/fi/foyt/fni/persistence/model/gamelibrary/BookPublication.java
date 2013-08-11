package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class BookPublication extends Publication {
	
	public PublicationFile getFile() {
		return file;
	}
	
	public void setFile(PublicationFile file) {
		this.file = file;
	}

	public Boolean getDownloadable() {
		return downloadable;
	}
	
	public void setDownloadable(Boolean downloadable) {
		this.downloadable = downloadable;
	}
	
	public Integer getNumberOfPages() {
		return numberOfPages;
	}
	
	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
	@ManyToOne
	private PublicationFile file;
	
  @Column (nullable=false, columnDefinition = "BIT")
  @NotNull
	private Boolean downloadable;
  
  private Integer numberOfPages;
}
