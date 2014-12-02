package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class IllusionEventDocument extends Document {
  
  public IllusionEventDocument() {
    setType(MaterialType.ILLUSION_GROUP_DOCUMENT);
  }
  
  public IllusionEventDocumentType getDocumentType() {
    return documentType;
  }
  
  public void setDocumentType(IllusionEventDocumentType documentType) {
    this.documentType = documentType;
  }
  
  public Integer getIndexNumber() {
    return indexNumber;
  }
  
  public void setIndexNumber(Integer indexNumber) {
    this.indexNumber = indexNumber;
  }
  
  @Enumerated (EnumType.STRING)
  @Column (nullable = false)
  private IllusionEventDocumentType documentType;
  
  @NotNull
  private Integer indexNumber;
}