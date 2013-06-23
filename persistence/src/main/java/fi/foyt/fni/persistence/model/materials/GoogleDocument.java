package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class GoogleDocument extends Material {
  
  public GoogleDocument() {
    setType(MaterialType.GOOGLE_DOCUMENT);
  }
  
  public String getDocumentId() {
    return documentId;
  }
  
  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }
  
  public GoogleDocumentType getDocumentType() {
    return documentType;
  }
  
  public void setDocumentType(GoogleDocumentType documentType) {
    this.documentType = documentType;
  }
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  private String documentId;
  
  @NotNull
  @Column (nullable = false)
  private GoogleDocumentType documentType;
}