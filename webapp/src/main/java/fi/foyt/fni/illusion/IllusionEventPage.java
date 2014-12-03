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
  
  
  public IllusionEventPage(String id, String urlName, String url, String title, String type, boolean editable, boolean deletable, boolean visibilityChangeable,
      boolean requiresUser, IllusionEventPageVisibility visibility) {
    super();
    this.id = id;
    this.urlName = urlName;
    this.url = url;
    this.title = title;
    this.type = type;
    this.editable = editable;
    this.deletable = deletable;
    this.visibilityChangeable = visibilityChangeable;
    this.requiresUser = requiresUser;
    this.visibility = visibility;
  }

  public String getId() {
    return id;
  }
  
  public String getUrlName() {
    return urlName;
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

  public boolean getRequiresUser() {
    return requiresUser;
  }
  
  private String id;
  private String urlName;
  private String url;
  private String title;
  private String type;
  private boolean editable;
  private boolean deletable;
  private boolean visibilityChangeable;
  private boolean requiresUser;
  private IllusionEventPageVisibility visibility;
}
