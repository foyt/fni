package fi.foyt.fni.persistence.model.materials;

import java.io.UnsupportedEncodingException;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringEscapeUtils;
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
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  @Transient
  public String getContentPlain() {
  	try {
	    String content = new String(data, "UTF-8");
	    return StringEscapeUtils.unescapeHtml4(content.replaceAll("\\<.*?>",""));
    } catch (UnsupportedEncodingException e) {
	    return null;
    }
  }
  
  @Column (length=1073741824)
  private byte[] data;
}