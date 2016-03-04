package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class Document extends Material {
  
  public Document() {
    setType(MaterialType.DOCUMENT);
  }
  
  public String getData() {
		return data;
	}
  
  public void setData(String data) {
		this.data = data;
	}
  
  @Transient
  public String getContentPlain() {
    return StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(getData().replaceAll("\\<.*?>"," ")));
  }

  @Column 
  @Lob
  private String data;
}