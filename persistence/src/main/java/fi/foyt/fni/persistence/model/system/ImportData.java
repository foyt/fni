package fi.foyt.fni.persistence.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ImportData {

  public Long getId() {
    return id;
  }
  
  public String getStrategy() {
    return strategy;
  }
  
  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }
  
  public ImportDataState getState() {
    return state;
  }
  
  public void setState(ImportDataState state) {
    this.state = state;
  }
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String strategy;
  
  @Column(nullable = false, length=1073741824)
  private byte[] data;
  
  @Column(nullable = false)
  @Enumerated (EnumType.ORDINAL)
  private ImportDataState state;
}
