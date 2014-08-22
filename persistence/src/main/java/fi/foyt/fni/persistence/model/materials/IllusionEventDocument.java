package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;

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
  
  @Enumerated (EnumType.STRING)
  @Column (nullable = false)
  private IllusionEventDocumentType documentType;
}