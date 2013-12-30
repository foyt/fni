package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Inheritance (strategy=InheritanceType.JOINED)
public class MaterialRevision {
  
  public Long getId() {
    return id;
  }
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public Date getCreated() {
    return created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public Boolean getCompressed() {
    return compressed;
  }
  
  public void setCompressed(Boolean compressed) {
    this.compressed = compressed;
  }
  
  public Boolean getCompleteRevision() {
    return completeRevision;
  }
  
  public void setCompleteRevision(Boolean completeRevision) {
    this.completeRevision = completeRevision;
  }
  
  public String getChecksum() {
		return checksum;
	}
  
  public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
  
  public Long getRevision() {
    return revision;
  }
  
  public void setRevision(Long revision) {
    this.revision = revision;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column
  @Lob
  private byte[] data;
  
  @Column (nullable = false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;
  
  @Column (nullable = false, columnDefinition = "BIT")
  private Boolean compressed;
  
  @Column (nullable = false, columnDefinition = "BIT")
  private Boolean completeRevision = Boolean.FALSE;
  
  private String checksum;
  
  @Column (nullable = false, updatable = false)
  private Long revision;
  
  private String sessionId;
}
