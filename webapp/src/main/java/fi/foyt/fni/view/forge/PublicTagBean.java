package fi.foyt.fni.view.forge;

public class PublicTagBean {

  public PublicTagBean(Long count, String text) {
    this.count = count;
    this.text = text;
  }
  
  public Long getCount() {
    return count;
  }
  
  public String getText() {
    return text;
  }
  
  private Long count;
  private String text;
}
