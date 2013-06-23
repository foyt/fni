package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class DropboxFile extends Material {
  
  public DropboxFile() {
    setType(MaterialType.DROPBOX_FILE);
  }
  
  public String getDropboxPath() {
    return dropboxPath;
  }
  
  public void setDropboxPath(String dropboxPath) {
    this.dropboxPath = dropboxPath;
  }
  
  public String getMimeType() {
    return mimeType;
  }
  
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  private String dropboxPath;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  private String mimeType;
}