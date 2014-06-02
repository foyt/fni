package fi.foyt.fni.coops;

import fi.foyt.coops.model.Patch;

public class PatchMessage {
  
  public PatchMessage() {
  }
  
  public Patch getData() {
    return data;
  }
  
  public String getType() {
    return type;
  }
  
  private Patch data;
  private String type;
}