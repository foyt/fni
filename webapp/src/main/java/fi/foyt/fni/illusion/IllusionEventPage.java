package fi.foyt.fni.illusion;

public class IllusionEventPage {
  
  public static enum Static {

    INDEX,
    
    MATERIALS,
    
    FORUM,

    PARTICIPANTS,
    
    GROUPS,
    
    SETTINGS,
    
    MANAGE_PAGES,
    
    MANAGE_TEMPLATES
    
  }
  
  public IllusionEventPage(String id, String urlName, String url, String title, String type) {
    this.id = id;
    this.urlName = urlName;
    this.url = url;
    this.title = title;
    this.type = type;
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
  
  private String id;
  private String urlName;
  private String url;
  private String title;
  private String type;
}
