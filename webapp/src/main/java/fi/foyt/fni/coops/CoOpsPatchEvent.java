package fi.foyt.fni.coops;

import fi.foyt.coops.model.Patch;

public class CoOpsPatchEvent {
  
  public CoOpsPatchEvent(String fileId, Patch patch) {
    this.fileId = fileId;
    this.patch = patch;
  }
  
  public String getFileId() {
    return fileId;
  }
  
  public Patch getPatch() {
    return patch;
  }

  private String fileId;
  private Patch patch;
}
