package fi.foyt.fni.utils.data;

import java.util.Date;

public class TypedData {
  
  public TypedData(byte[] data, String contentType, Date modified) {
    this.data = data;
    this.contentType = contentType;
    this.modified = modified;
  }

  public TypedData(byte[] data, String contentType) {
    this(data, contentType, new Date(System.currentTimeMillis()));
  }
  
  public byte[] getData() {
    return data;
  }
  
  public String getContentType() {
    return contentType;
  }
  
  public Date getModified() {
    return modified;
  }
  
  private byte[] data;
  private String contentType;
  private Date modified;
}
