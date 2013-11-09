package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class BookPublication extends Publication {
	
	public PublicationFile getPrintableFile() {
    return printableFile;
  }
	
	public void setPrintableFile(PublicationFile printableFile) {
    this.printableFile = printableFile;
  }
	
	public PublicationFile getDownloadableFile() {
    return downloadableFile;
  }
	
	public void setDownloadableFile(PublicationFile downloadableFile) {
    this.downloadableFile = downloadableFile;
  }
	
	public Integer getNumberOfPages() {
		return numberOfPages;
	}
	
	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
	@ManyToOne
	private PublicationFile downloadableFile;

  @ManyToOne
  private PublicationFile printableFile;
  
  private Integer numberOfPages;
}
