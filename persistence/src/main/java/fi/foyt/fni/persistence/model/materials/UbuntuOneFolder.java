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
public class UbuntuOneFolder extends Folder {
  
  public UbuntuOneFolder() {
    setType(MaterialType.DROPBOX_FOLDER);
  }
  
  public String getUbuntuOneKey() {
    return ubuntuOneKey;
  }
  
  public void setUbuntuOneKey(String ubuntuOneKey) {
    this.ubuntuOneKey = ubuntuOneKey;
  }
  
  public Long getGeneration() {
    return generation;
  }
  
  public void setGeneration(Long generation) {
    this.generation = generation;
  }
  
  public String getContentPath() {
    return contentPath;
  }
  
  public void setContentPath(String contentPath) {
    this.contentPath = contentPath;
  }
  
  @NotNull
  @NotEmpty
  @Column (nullable = false, unique = true)
  private String ubuntuOneKey;
  
  @NotNull
  @Column (nullable = false)
  private Long generation;

  @NotNull
  @Column (nullable = false)
  private String contentPath;
}