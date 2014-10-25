package fi.foyt.fni.illusion;

public class IllusionEventPage {
  
  public static enum Static {

    INDEX,
    
    MATERIALS,
    
    PARTICIPANTS,
    
    GROUPS,
    
    SETTINGS,
    
    MANAGE_PAGES
    
  }
  
  
  public IllusionEventPage(String id, String url, String title, String type, boolean editable, boolean deletable, boolean visibilityChangeable,
      IllusionEventPageVisibility visibility) {
    super();
    this.id = id;
    this.url = url;
    this.title = title;
    this.type = type;
    this.editable = editable;
    this.deletable = deletable;
    this.visibilityChangeable = visibilityChangeable;
    this.visibility = visibility;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getType() {
    return type;
  }

  public IllusionEventPageVisibility getVisibility() {
    return visibility;
  }

  public boolean getDeletable() {
    return deletable;
  }

  public boolean getEditable() {
    return editable;
  }

  public boolean getVisibilityChangeable() {
    return visibilityChangeable;
  }

  private String id;
  private String url;
  private String title;
  private String type;
  private boolean editable;
  private boolean deletable;
  private boolean visibilityChangeable;
  private IllusionEventPageVisibility visibility;
}
