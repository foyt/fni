package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

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
	
	public Long getDownloadCount() {
    return downloadCount;
  }
	
	public void setDownloadCount(Long downloadCount) {
    this.downloadCount = downloadCount;
  }
	
	public Long getPrintCount() {
    return printCount;
  }
	
	public void setPrintCount(Long printCount) {
    this.printCount = printCount;
  }
	
	@ManyToOne (fetch = FetchType.LAZY)
	private PublicationFile downloadableFile;

  @ManyToOne (fetch = FetchType.LAZY)
  private PublicationFile printableFile;
  
  private Integer numberOfPages;
  
  @Column (nullable = false)
  @NotNull  
  private Long downloadCount;
  
  @Column (nullable = false)
  @NotNull
  private Long printCount;
}
