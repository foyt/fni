package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Indexed
public class VectorImage extends Material {
  
  public VectorImage() {
    setType(MaterialType.VECTOR_IMAGE);
  }
  
  public String getData() {
	  return data;
  }
  
  public void setData(String data) {
	  this.data = data;
  }
  
  @Lob
  private String data;
}
