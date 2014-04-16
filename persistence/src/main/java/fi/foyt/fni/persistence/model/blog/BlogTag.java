package fi.foyt.fni.persistence.model.blog;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class BlogTag {

  public Long getId() {
    return id;
  }

  public String getText() {
		return text;
	}
  
  public void setText(String text) {
		this.text = text;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotEmpty
  @Column (nullable=false)
  private String text;
}