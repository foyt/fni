package fi.foyt.fni.upload;

public class UploadInfoFile {

  public UploadInfoFile(String fieldName) {
    this.fieldName = fieldName;
    this.status = Status.PENDING;
  }
 
  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Status getStatus() {
	  return status;
  }
  
  public void setStatus(Status status) {
	  this.status = status;
  }
 
  public long getUploaded() {
    return uploaded;
  }
 
  public void incUploaded(long bytes) {
    uploaded += bytes;
  }
 
  public double getProcessed() {
    return processed;
  }
 
  public void setProcessed(double processed) {
    this.processed = processed;
  }

  private String fieldName;
  private Status status;
  private long uploaded = 0;  
  private double processed = 0;
  
  public enum Status {
  	PENDING,
  	UPLOADING,
  	PROCESSING,
  	COMPLETE,
  	FAILED
  }
}