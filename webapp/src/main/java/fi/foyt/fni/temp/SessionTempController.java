package fi.foyt.fni.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;

import org.apache.commons.io.IOUtils;

@SessionScoped
@Stateful
public class SessionTempController {

  @PostConstruct
  public void postConstruct() {
    tempFileIds = new ArrayList<String>();
  }
  
  @PreDestroy
  public void preDestroy() {
    for (String tempFileId : tempFileIds) {
      deleteTempFile(tempFileId);
    }
  }
  
  public String createTempFile(InputStream inputStream) throws IOException {
    File tempFile = createNewTempFile();

    FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
    try {
      IOUtils.copy(inputStream, tempFileOutputStream);
    } finally {
      tempFileOutputStream.flush();
      tempFileOutputStream.close();
    }
    
    return tempFile.getName();
  }
  
  public byte[] getTempFileData(String fileId) throws IOException {
    File file = new File(getTempDirectory(), fileId);
    FileInputStream fileInputStream = new FileInputStream(file);
    try {
      return IOUtils.toByteArray(fileInputStream);
    } finally {
      fileInputStream.close();
    }
  }
  
  public void deleteTempFile(String fileId) {
    File file = new File(getTempDirectory(), fileId);
    file.delete();
  }
  
  private File createNewTempFile() throws IOException {
    File file = new File(getTempDirectory(), UUID.randomUUID().toString());
    file.createNewFile();
    tempFileIds.add(file.getName());
    return file;
  }
  
  private File getTempDirectory() {
    return new File(System.getProperty("java.io.tmpdir"));
  }

  private List<String> tempFileIds;
}
