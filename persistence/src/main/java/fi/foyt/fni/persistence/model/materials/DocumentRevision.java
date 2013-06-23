package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@PrimaryKeyJoinColumn (name="id")
public class DocumentRevision extends MaterialRevision {
  
  public Document getDocument() {
    return document;
  }
  
  public void setDocument(Document document) {
    this.document = document;
  }
  
  @ManyToOne
  private Document document;
}
