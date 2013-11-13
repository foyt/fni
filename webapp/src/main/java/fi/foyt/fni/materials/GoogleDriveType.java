package fi.foyt.fni.materials;

public enum GoogleDriveType {
  AUDIO ("application/vnd.google-apps.audio"),
  DOCUMENT ("application/vnd.google-apps.document"), 
  DRAWING ("application/vnd.google-apps.drawing"),
  FILE ("application/vnd.google-apps.file"),
  FOLDER ("application/vnd.google-apps.folder"),
  FORM ("application/vnd.google-apps.form"),
  FUSIONTABLE ("application/vnd.google-apps.fusiontable"),
  PHOTO ("application/vnd.google-apps.photo"),
  PRESENTATION ("application/vnd.google-apps.presentation"), 
  SCRIPT ("application/vnd.google-apps.script"),
  SITES ("application/vnd.google-apps.sites"),
  SPREADSHEET ("application/vnd.google-apps.spreadsheet"), 
  UNKNOWN ("application/vnd.google-apps.unknown"),
  VIDEO ("application/vnd.google-apps.video");
  
  private GoogleDriveType(String mimeType) {
    this.mimeType = mimeType;
  }
  
  public static GoogleDriveType findByMimeType(String mimeType) {
    for (GoogleDriveType type : values()) {
      if (type.getMimeType().equals(mimeType)) {
        return type;
      }
    }
    
    return null;
  }
  
  public String getMimeType() {
    return mimeType;
  }
  
  private String mimeType;
}