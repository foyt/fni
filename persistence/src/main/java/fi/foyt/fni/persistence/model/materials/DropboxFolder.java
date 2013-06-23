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
public class DropboxFolder extends Folder {
  
  public DropboxFolder() {
    setType(MaterialType.DROPBOX_FOLDER);
  }
  
  public String getDropboxPath() {
    return dropboxPath;
  }
  
  public void setDropboxPath(String dropboxPath) {
    this.dropboxPath = dropboxPath;
  }
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  private String dropboxPath;
}