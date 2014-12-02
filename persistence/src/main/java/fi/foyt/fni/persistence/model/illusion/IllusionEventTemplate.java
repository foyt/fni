package fi.foyt.fni.persistence.model.illusion;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Entity
public class IllusionEventTemplate {

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public Date getModified() {
    return modified;
  }
  
  public void setModified(Date modified) {
    this.modified = modified;
  }
  
  public IllusionEvent getEvent() {
    return event;
  }
  
  public void setEvent(IllusionEvent event) {
    this.event = event;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String name;
  
  @Lob
  private String data;

  @Column (nullable = false)
  @NotNull
  private Date modified;
  
  @ManyToOne
  private IllusionEvent event;
}
