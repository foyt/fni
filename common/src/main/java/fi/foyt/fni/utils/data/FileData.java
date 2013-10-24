package fi.foyt.fni.utils.data;

import java.util.Date;


public class FileData extends TypedData {

  public FileData(String fieldName, String fileName, byte[] data, String contentType, Date modified) {
    super(data, contentType, modified);
    
    this.fileName = fileName;
    this.fieldName = fieldName;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public String getFieldName() {
    return fieldName;
  }
  
  private String fieldName;
  private String fileName;
}
