package fi.foyt.fni.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class MonitoredDiskFileItemFactory extends DiskFileItemFactory {

  public MonitoredDiskFileItemFactory(int sizeThreshold, File repository, UploadInfo uploadInfo) {
    super(sizeThreshold, repository);
    this.uploadInfo = uploadInfo;
  }

  public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
    FileItem fileItem = null;

    if (!StringUtils.isBlank(fileName)) {
      UploadInfoFile infoFile = new UploadInfoFile(fieldName);
      this.uploadInfo.getFiles().add(infoFile);
      UploadFileListener listener = new UploadFileListener(infoFile);
      fileItem = new MonitoredDiskFileItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(), getRepository(), listener);
    } else {
      fileItem = super.createItem(fieldName, contentType, isFormField, fileName);
    }
    
    return fileItem;
  }

  private UploadInfo uploadInfo;
}
