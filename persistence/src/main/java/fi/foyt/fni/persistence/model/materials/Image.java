package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@Cacheable (true)
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class Image extends Binary {

  public Image() {
    setType(MaterialType.IMAGE);
  }
  
}
