package fi.foyt.fni.coops;

import fi.foyt.coops.model.Patch;

public class UpdateMessage {
  
  public UpdateMessage(Patch patch) {
    this.data = patch;
    this.type = "update";
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