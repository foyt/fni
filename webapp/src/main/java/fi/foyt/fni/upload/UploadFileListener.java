package fi.foyt.fni.upload;

import fi.foyt.fni.upload.UploadInfoFile.Status;

public class UploadFileListener implements OutputStreamListener {

  public UploadFileListener(UploadInfoFile uploadInfoFile) {
    this.uploadInfoFile = uploadInfoFile;    
  }
  
  @Override
  public void bytesRead(int bytesRead) {
  	uploadInfoFile.incUploaded(bytesRead);
  }

  @Override
  public void done() {
    uploadInfoFile.setStatus(Status.PROCESSING);
  }

  @Override
  public void error(String message) {
    uploadInfoFile.setStatus(Status.FAILED);
  }

  @Override
  public void start() {
    uploadInfoFile.setStatus(Status.UPLOADING);
  }

  private UploadInfoFile uploadInfoFile;
}
