package fi.foyt.fni.utils.fileupload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import fi.foyt.fni.utils.streams.StreamUtils;

public class FileUploadUtils {
	
  public static List<FileData> getRequestFileItems(ServletFileUpload upload, HttpServletRequest httpServletRequest, boolean returnFormFields) throws IOException, FileUploadException {
    List<FileData> result = new ArrayList<FileData>();

    List<FileItem> fileItems = upload.parseRequest(httpServletRequest);
    for (FileItem fileItem : fileItems) {
      String fieldName = fileItem.getFieldName();
      String fileName = fileItem.getName();
      byte[] data = StreamUtils.getInputStreamAsBytes(fileItem.getInputStream());
      String contentType = fileItem.getContentType();
      
      result.add(new FileData(fieldName, fileName, data, contentType, new Date()));
    }
    
    return result;
  }

}
