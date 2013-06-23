package fi.foyt.fni.persistence.model.store;

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
	
  @Column (nullable=false, columnDefinition = "BIT")
  @NotNull
	private Boolean downloadable;
}
